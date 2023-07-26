package dev.corgitaco.examplemod.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Used to dynamically create a randomly generated path from 1 object to another.
 */
public class NoiseWormPathGenerator {
    private final List<Node> nodes;

    public NoiseWormPathGenerator(List<Node> nodes) {
        this.nodes = nodes;
    }

    public NoiseWormPathGenerator(NormalNoise noise, BlockPos startPos, Predicate<BlockPos> isInvalid, int maxDistance) {
        List<Node> nodes = new ArrayList<>();

        nodes.add(new Node(startPos.immutable(), 0));
        int distanceInNodes = maxDistance / 5;

        for (int i = 1; i < distanceInNodes; i++) {
            Node prevNode = nodes.get(i - 1);
            double angle = noise.getValue(prevNode.getPos().getX(), 0, prevNode.getPos().getZ());
            BlockPos previousNodePos = prevNode.getPos();

            Vector2d dAngle = get2DAngle(angle * 5, 25);
            Vec3i vecAngle = new Vec3i((int) dAngle.x, 0, (int) dAngle.y);


            BlockPos addedPos = previousNodePos.offset(vecAngle);


            int seed = 1;
            while (previousNodePos.closerThan(addedPos, 5)) {
                angle = noise.getValue(prevNode.getPos().getX(), seed, prevNode.getPos().getZ());
                dAngle = get2DAngle(angle * 5, 25);
                vecAngle = new Vec3i((int) dAngle.x, 0, (int) dAngle.y);
                addedPos = previousNodePos.offset(vecAngle);
            }


            int newY = 0;
            BlockPos pos = new BlockPos(addedPos.getX(), newY, addedPos.getZ());

            Node nextNode = new Node(pos.immutable(), i);

            if (isInvalid.test(nextNode.getPos())) {
                break;
            }
            long key = ChunkPos.asLong(SectionPos.blockToSectionCoord(nextNode.getPos().getX()), SectionPos.blockToSectionCoord(nextNode.getPos().getZ()));

            nodes.add(nextNode);
        }

        this.nodes = nodes;
    }

    public boolean exists() {
        return nodes != null;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public BlockPos getFinalPosition() {
        return this.nodes.get(this.nodes.size() - 1).getPos();
    }

    public int getTotalNumberOfNodes() {
        return this.nodes.size();
    }

    public BlockPos getStartPos() {
        return this.nodes.get(0).getPos();
    }

    public static Vector2d get2DAngle(double angle, float length) {
        double x = (Math.sin(angle) * length);
        double y = (Math.cos(angle) * length);

        return new Vector2d(x, y);
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        ListTag positions = new ListTag();
        for (Node node : this.nodes) {
            CompoundTag posNBT = new CompoundTag();
            posNBT.putInt("idx", node.getIdx());
            posNBT.putIntArray("pos", writeBlockPos(node.getPos().immutable()));
            positions.add(posNBT);
        }
        nbt.put("nodes", positions);
        return nbt;
    }

    public static NoiseWormPathGenerator read(CompoundTag nbt) {
        List<Node> nodes = new ArrayList<>();

        ListTag nodeNBTList = nbt.getList("nodes", 10);
        for (Tag tag : nodeNBTList) {
            nodes.add(new Node(getBlockPos(((CompoundTag) tag).getIntArray("pos")).immutable(), ((CompoundTag) tag).getInt("idx")));
        }

        return new NoiseWormPathGenerator(nodes);

    }

    public int[] writeBlockPos(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos getBlockPos(int[] posArray) {
        return new BlockPos(posArray[0], posArray[1], posArray[2]);
    }


    public static class Node {
        private final int idx;
        private final BlockPos pos;

        private Node(BlockPos pos, int idx) {
            this.pos = pos;
            this.idx = idx;
        }

        public BlockPos getPos() {
            return pos;
        }

        public int getIdx() {
            return idx;
        }
    }
}