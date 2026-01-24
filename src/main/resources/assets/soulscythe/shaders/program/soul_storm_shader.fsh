#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InverseProjectionMatrix;
uniform mat4 InverseViewMatrix;

uniform vec3 PlayerPos;     // world-space player position
uniform float NearRadius;   // inner radius for grayscale+magenta
uniform float FarRadius;    // outer radius for dark blue/purple

uniform vec3 GrayTint;
uniform vec3 MagentaColor;
uniform vec3 OuterColor;    // dark blue/purple

uniform float maxMagenta;
uniform float minMagenta;

uniform float OutlineThickness; // e.g. 1.0
uniform float OutlineStrength;  // e.g. 1.0

in vec2 texCoord;
out vec4 fragColor;

// Convert depth to world-space position
vec3 getWorldPos(vec2 uv, float depth) {
    vec4 clip = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 view = InverseProjectionMatrix * clip;
    view /= view.w;
    vec4 world = InverseViewMatrix * view;
    return world.xyz;
}

float getEdgeStrength(vec2 uv) {
    vec2 texel = OutlineThickness / vec2(textureSize(DiffuseSampler, 0));

    float centerDepth = texture(DepthSampler, uv).r;
    vec3 centerColor = texture(DiffuseSampler, uv).rgb;

    float depthEdge = 0.0;
    float colorEdge = 0.0;

    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            if (x == 0 && y == 0) continue;

            vec2 offset = vec2(x, y) * texel;

            float d = texture(DepthSampler, uv + offset).r;
            vec3 c = texture(DiffuseSampler, uv + offset).rgb;

            depthEdge = max(depthEdge, abs(centerDepth - d));
            colorEdge = max(colorEdge, length(centerColor - c));
        }
    }

    // Boost depth edges (silhouettes)
    depthEdge *= 8.0;

    // Color edges stay subtle
    colorEdge *= 1.0;

    // Store BOTH in one value using distance later
    return clamp(depthEdge + colorEdge, 0.0, 1.0);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Grayscale
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 grayscale = vec3(gray) * GrayTint;

    // Magenta detection
    float magentaStrength = min(color.r, color.b) - color.g;
    float isMagenta = smoothstep(minMagenta, maxMagenta, magentaStrength);

    // Reconstruct world position
    float depth = texture(DepthSampler, texCoord).r;
    vec3 worldPos = getWorldPos(texCoord, depth);

    // Distance from player in XZ plane
    float dist = length(worldPos.xz - PlayerPos.xz);

    // --- Radius-based blending ---
    vec3 nearEffect = mix(grayscale, color.rgb, isMagenta); // inner radius effect

    // Normalize distance for smooth blending
    float distNormalized = clamp((dist - NearRadius) / (FarRadius - NearRadius), 0.0, 1.0);

    // finalColor = nearEffect close to player, OuterColor far away
    vec3 finalColor = mix(nearEffect, OuterColor, distNormalized);

    // --- Horizon line ---
    float horizonStart = 0.990;
    float horizonEnd   = 0.995;
    float horizonMask = smoothstep(horizonStart, horizonEnd, depth);

    // Inner radius: grayscale + magenta
    if (dist <= NearRadius) {
        finalColor = mix(grayscale, color.rgb, isMagenta);
    }
    // Outer radius: dark blue/purple
    else if (dist <= FarRadius) {
        // Smooth blend from inner radius edge to outer radius
        float t = (dist - NearRadius) / (FarRadius - NearRadius);
        vec3 outerEffect = OuterColor;
        finalColor = mix(mix(grayscale, color.rgb, isMagenta), outerEffect, t);
    }
    // Beyond FarRadius: solid OuterColor
    else {
        finalColor = OuterColor;
    }

    // --- MAGENTA OUTLINE ---
    float edge = getEdgeStrength(texCoord);

    // Distance fade: 0 = near, 1 = far
    float distFade = clamp((dist - NearRadius) / (FarRadius - NearRadius), 0.0, 1.0);

    // Near: allow noisy pixel edges
    float nearEdge = edge;

    // Far: suppress weak edges (kills pixel noise)
    float farEdge = smoothstep(0.15, 0.4, edge);

    // Blend behavior based on distance
    float finalEdge = mix(nearEdge, farEdge, distFade);

    // Magenta things still pop more
    finalEdge *= mix(0.6, 1.4, isMagenta);

    // Outline color
    vec3 outlineColor = vec3(1.0, 0.0, 1.0);

    // Apply outline
    finalColor = mix(finalColor, outlineColor, finalEdge * OutlineStrength);

    fragColor = vec4(finalColor, color.a);
}
