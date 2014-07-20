vec2 calcParallaxTexCoords(sampler2D dispMap, mat3 tbnMatrix, vec3 directionToEye, vec2 texCoords, float scale, float bias)
{
	return texCoords.xy + normalize(directionToEye * tbnMatrix).xy * vec2(texture2D(dispMap, texCoords.xy).r * scale + bias);
}

float sampleShadowMap(sampler2D shadowMap, vec2 texCoords, float currentDepth)
{
	return step(currentDepth, texture2D(shadowMap, texCoords.xy).r);
}

float sampleShadowMapLinear(sampler2D shadowMap, vec2 texCoords, float currentDepth, vec2 texelSize)
{
	vec2 pixelPos = texCoords/texelSize + vec2(0.5);
	vec2 fractPart = fract(pixelPos);
	vec2 startTexel = (pixelPos - fractPart) * texelSize;

	float bottomLeft = sampleShadowMap(shadowMap, startTexel, currentDepth);
	float bottomRight = sampleShadowMap(shadowMap, startTexel + vec2(texelSize.x, 0), currentDepth);
	float topLeft = sampleShadowMap(shadowMap, startTexel + vec2(0, texelSize.y), currentDepth);
	float topRight = sampleShadowMap(shadowMap, startTexel + texelSize, currentDepth);

	float a = mix(bottomLeft, bottomRight, fractPart.x);
	float b = mix(topLeft, topRight, fractPart.x);

	return mix(a,b,fractPart.y);
}

float sampleShadowMapLinearPCF(sampler2D shadowMap, vec2 texCoords, float currentDepth, vec2 texelSize)
{
	const float SAMPLES = 3.0f;
	const float SAMPLES_START = (SAMPLES-1.0f)/2.0f;
	const float SAMPLES_SQUARED = SAMPLES*SAMPLES;
	float result = 0.0f;

	for(float y = -SAMPLES_START; y <= SAMPLES_START; y+=1.0f)
	{
		for(float x = -SAMPLES_START; x <= SAMPLES_START; x+=1.0f)
		{
			vec2 offset = vec2(x,y)*texelSize;
			result += sampleShadowMapLinear(shadowMap, texCoords+offset, currentDepth, texelSize);
		}
	}

	return result/SAMPLES_SQUARED;
}

float linstep(float low, float high, float v)
{
	return clamp((v-low)/(high-low) ,0.0,1.0);
}

float sampleVarianceShadowMap(sampler2D shadowMap, vec2 texCoords, float currentDepth, float varianceMin, float lightBleedReduction)
{
	vec2 moments = texture2D(shadowMap, texCoords.xy).xy;

	float p = step(currentDepth, moments.x);
	float variance = max(moments.y - moments.x * moments.x, varianceMin);

	float distance = currentDepth - moments.x;
	float pMax = linstep(lightBleedReduction,1.0,variance / (variance+distance*distance));

	return min(max(p, pMax), 1.0);
	//return step(currentDepth, texture2D(shadowMap, texCoords.xy).r);
}