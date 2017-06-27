/*
* Copyright (c) 2017 Andrew Yeung
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

#pragma version(1)
#pragma rs java_package_name(com.noodlesandnaan.renderscript.scripts.BlendLinearGradient)

#pragma rs_fp_relaxed

static rs_script gScript;
static rs_allocation gIn;
static rs_allocation gInBlurred;
static rs_allocation gOut;

static int gBmpWidth;
static int gBmpHeight;
static float2 gCenter;

static float gHalfWidthInnerNorm;
static float gHalfWidthOuterNorm;
static float gHalfWidthDeltaNorm;

static float2 gRotateNormal;

void setIOBlendLinearGradient(rs_script script, rs_allocation in, rs_allocation inBlurred, rs_allocation out) {
	gScript = script;
	gIn = in;
	gInBlurred = inBlurred;
	gOut = out;

	gBmpWidth = rsAllocationGetDimX(gIn);
	gBmpHeight = rsAllocationGetDimY(gIn);
	gCenter[0] = gBmpWidth / 2.0f;
	gCenter[1] = gBmpHeight / 2.0f;
}

void setParamsBlendLinearGradient(float widthInnerNorm, float widthOuterNorm, float radians) {
	gHalfWidthInnerNorm = clamp(widthInnerNorm / 2.0f, 0.0f, widthOuterNorm / 2.0f);
	gHalfWidthOuterNorm = widthOuterNorm / 2.0f;
	gHalfWidthDeltaNorm = gHalfWidthOuterNorm - gHalfWidthInnerNorm;

	gRotateNormal[0] = sin(-radians);
	gRotateNormal[1] = cos(-radians);
}

void execute() {
	rsForEach(gScript, gIn, gOut);
}

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {

	float xNorm = (float) x / gBmpWidth;
	float yNorm = (float) y / gBmpHeight;

	float2 vecToPixel = { xNorm - 0.5f, yNorm - 0.5f };
	float distFromCenter = fabs(dot(normalize(gRotateNormal), vecToPixel));

	float blendFactor = clamp((distFromCenter - gHalfWidthInnerNorm) / gHalfWidthDeltaNorm, 0.0f, 1.0f);

	//rsDebug("HIHI", blendFactor);

	//float4 pixelOut = { blendFactor, blendFactor, blendFactor, 1.0f };

	float4 pixelIn = rsUnpackColor8888(in);
	float4 pixelInBlurred = rsUnpackColor8888(*(uchar4*) rsGetElementAt(gInBlurred, x, y));
	
	float4 pixelOut = mix(pixelIn, pixelInBlurred, blendFactor);

	return rsPackColorTo8888(pixelOut);
}
