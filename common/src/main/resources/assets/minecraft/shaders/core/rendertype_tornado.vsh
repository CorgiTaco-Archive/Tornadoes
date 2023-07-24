#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 WorldPos;
in vec3 Settings;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord0;
out vec3 worldPosition;
out vec3 settings;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;

    worldPosition = WorldPos;
    settings = Settings;
}
