#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

const vec4 colors[] = vec4[8](vec4(1.0, 0.0, 0.0, 1.0), vec4(1.0, 0.5, 0.0, 1.0), vec4(1.0, 1.0, 0.0, 1.0), vec4(0.0, 1.0, 0.0, 1.0), vec4(0.0, 0.0, 1.0, 1.0), vec4(0.3, 0.0, 0.5, 1.0), vec4(0.5, 0.0, 1.0, 1.0), vec4(1.0, 0.0, 0.0, 1.0));
const vec2 direction = vec2(1.0, 1.0);
const float speed = 1.0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

vec4 SMOOTHY(float x) {
    x *= (colors.length() - 1);
    return mix(colors[int(x)], colors[int(x) + 1], smoothstep(0.0, 1.0, fract(x)));
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);

    if (length(color.rgb) != 0.0) {
        vec2 coords = gl_FragCoord.xy;
        color *= vec4(SMOOTHY(float(int(length(coords + (direction * GameTime * 24000 * speed) * 2)) % 500) / 500.0).rgb, 1) * vertexColor;
    }

    // color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
