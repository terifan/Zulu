Texture2D basemap, normalmap, lightmap;

function init()
{
	loadTexture("C:/java/src/dev/zulu/samples/resources/011.64713876.DXT1.png", basemap);
	loadTexture("C:/java/src/dev/zulu/samples/resources/011.77599217.DXT1.png", normalmap);
	loadGeometryTexture(0, lightmap);
}

function vertexShader(vec3 coord, vec3 color, vec2 tex1, vec2 tex2, vec2 tex3, vec3 tex4, vec aux)
{
	Output out;

	out.coord = coord;
	out.tex1 = tex1;
}

function geometryShader(Vertex v0, Vertex v1, Vertex v2)
{
	Vertex v01, v02, v12;

	interpolate(v0, v1, v01, 0.5);
	interpolate(v0, v2, v02, 0.5);
	interpolate(v1, v2, v12, 0.5);

	triangle(v0,  v01, v02);
	triangle(v01, v12, v02);
	triangle(v01, v1,  v12);
	triangle(v02, v12, v2);
}

function pixelShader(vec3 coord, vec2 tex1)
{
	Frame frame;

	if (coord.z <= frame.depth)
	{
		vec3 texel, lumel, color;

		sample2D(basemap, tex1, texel);
		sample2D(lightmap, tex2, lumel);

		mul(texel, lumel, color);

		frame.color = color;
		frame.depth = coord.z;
	}
}
