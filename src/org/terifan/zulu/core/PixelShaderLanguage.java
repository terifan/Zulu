package org.terifan.zulu.core;

import org.terifan.util.script.Branch;
import org.terifan.util.script.Function;
import org.terifan.util.script.Keyword;
import org.terifan.util.script.Language;
import org.terifan.util.script.Operator;
import org.terifan.util.script.Primitive;


class PixelShaderLanguage extends Language
{
	public final Keyword 
		IMPORT = add(new Keyword("import")),
		USING = add(new Keyword("using")),
		INPUT = add(new Keyword("input")),
		CONST = add(new Keyword("const")),
		FRAGCOORD = add(new Keyword("g_FragCoord")),
		FRAGCOLOR = add(new Keyword("g_FragColor")),
		FRAGDEPTH = add(new Keyword("g_FragDepth")),
		FRONTFACING = add(new Keyword("g_FrontFacing")),
		OVERBRIGHTFACTOR = add(new Keyword("g_OverbrightFactor")),
		GAMMA = add(new Keyword("g_Gamma")),
		BRIGHTNESS = add(new Keyword("g_Brightness")),
		FOR = add(new Keyword("for"));

	public final Primitive
		VOID = add(new Primitive("void")),
		BOOLEAN = add(new Primitive("boolean")),
		BYTE = add(new Primitive("byte")),
		SHORT = add(new Primitive("short")),
		CHAR = add(new Primitive("char")),
		INT = add(new Primitive("int")),
		LONG = add(new Primitive("long")),
		FLOAT = add(new Primitive("float")),
		DOUBLE = add(new Primitive("double")),
		STRING = add(new Primitive("String"));

	public final Operator
		OP_SEMI = add(new Operator(";")),
		OP_COMMA = add(new Operator(",")),
		OP_DOT = add(new Operator(".")),
		OP_EQ = add(new Operator("=")),
		OP_GT = add(new Operator(">")),
		OP_LT = add(new Operator("<")),
		OP_BANG = add(new Operator("!")),
		OP_TILDE = add(new Operator("~")),
		OP_QUES = add(new Operator("?")),
		OP_COLON = add(new Operator(":")),
		OP_EQEQ = add(new Operator("==")),
		OP_LTEQ = add(new Operator("<=")),
		OP_GTEQ = add(new Operator(">=")),
		OP_BANGEQ = add(new Operator("!=")),
		OP_AMPAMP = add(new Operator("&&")),
		OP_BARBAR = add(new Operator("||")),
		OP_PLUSPLUS = add(new Operator("++")),
		OP_SUBSUB = add(new Operator("--")),
		OP_PLUS = add(new Operator("+")),
		OP_SUB = add(new Operator("-")),
		OP_STAR = add(new Operator("*")),
		OP_SLASH = add(new Operator("/")),
		OP_AMP = add(new Operator("&")),
		OP_BAR = add(new Operator("|")),
		OP_CARET = add(new Operator("^")),
		OP_PERCENT = add(new Operator("%")),
		OP_LTLT = add(new Operator("<<")),
		OP_GTGT = add(new Operator(">>")),
		OP_GTGTGT = add(new Operator(">>>")),
		OP_PLUSEQ = add(new Operator("+=")),
		OP_SUBEQ = add(new Operator("-=")),
		OP_STAREQ = add(new Operator("*=")),
		OP_SLASHEQ = add(new Operator("/=")),
		OP_AMPEQ = add(new Operator("&=")),
		OP_BAREQ = add(new Operator("|=")),
		OP_CARETEQ = add(new Operator("^=")),
		OP_PERCENTEQ = add(new Operator("%=")),
		OP_LTLTEQ = add(new Operator("<<=")),
		OP_GTGTEQ = add(new Operator(">>=")),
		OP_GTGTGTEQ = add(new Operator(">>>="));

	public final Branch
		LPAREN = add(new Branch("(", ")", false)),
		RPAREN = add(new Branch(")", "(", true)),
		LBRACE = add(new Branch("{", "}", false)),
		RBRACE = add(new Branch("}", "{", true)),
		LBRACKET = add(new Branch("[", "]", false)),
		RBRACKET = add(new Branch("]", "[", true));

	public final Function
		TEXTURE1D = add(new Function("tex1d")),
		TEXTURE2D = add(new Function("tex2d")),
		TEXTURE3D = add(new Function("tex3d")),
		TEXCOORD = add(new Function("texcoord")),
		ADD = add(new Function("add")),
		SUB = add(new Function("sub")),
		MUL = add(new Function("mul")),
		MIN = add(new Function("min")),
		MAX = add(new Function("max")),
		DOT = add(new Function("dot")),
		CROSS = add(new Function("cross")),
		DISTANCE = add(new Function("distance")),
		LENGTH = add(new Function("length")),
		NORMALIZE = add(new Function("normalize")),
		POW = add(new Function("pow")),
		EXP = add(new Function("exp")),
		LOG = add(new Function("log")),
		EXP2 = add(new Function("exp2")),
		LOG2 = add(new Function("log2")),
		RADIANS = add(new Function("radians")),
		DEGREES = add(new Function("degrees")),
		SIN = add(new Function("sin")),
		COS = add(new Function("cos")),
		TAN = add(new Function("tan")),
		ASIN = add(new Function("asin")),
		ACOS = add(new Function("acos")),
		ATAN = add(new Function("atan")),
		SQRT = add(new Function("sqrt")),
		INVERSESQRT = add(new Function("inversesqrt")),
		ABS = add(new Function("abs")),
		SIGN = add(new Function("sign")),
		MIX = add(new Function("mix")),
		FLOOR = add(new Function("floor")),
		CEIL = add(new Function("ceil")),
		FRACT = add(new Function("fract")),
		MOD = add(new Function("mod")),
		CLAMP = add(new Function("clamp")),
		STEP = add(new Function("step")),
		SMOOTHSTEP = add(new Function("smoothstep")),
		NOISE1 = add(new Function("noise1")),
		NOISE2 = add(new Function("noise2")),
		NOISE3 = add(new Function("noise3")),
		NOISE4 = add(new Function("noise4"));

	public final static PixelShaderLanguage instance = new PixelShaderLanguage();

	private PixelShaderLanguage()
	{
	}
}