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
#pragma rs java_package_name(com.noodlesandnaan.renderscript.scripts.Vignette)

#pragma rs_fp_relaxed

static rs_script gScript;
static rs_allocation gIn;
static rs_allocation gOut;

static int gBmpWidth;
static int gBmpHeight;
static float2 gCenter;

static float gIntensityNorm;
static float gRadiusInnerNorm;
static float gRadiusOuterNorm;
static float gRadiusDeltaNorm;

void setIOVignette(rs_script script, rs_allocation in, rs_allocation out) {
	gScript = script;
	gIn = in;
	gOut = out;

	gBmpWidth = rsAllocationGetDimX(gIn);
	gBmpHeight = rsAllocationGetDimY(gIn);
	gCenter[0] = gBmpWidth / 2.0f;
	gCenter[1] = gBmpHeight / 2.0f;
}

void setParamsVignette(float intensityNorm, float radiusInnerNorm, float radiusOuterNorm) {
	gIntensityNorm = intensityNorm;
	gRadiusInnerNorm = (float) clamp(radiusInnerNorm, 0.0f, radiusOuterNorm);
	gRadiusOuterNorm = radiusOuterNorm;
	gRadiusDeltaNorm = (float) clamp(radiusOuterNorm - radiusInnerNorm, 0.0f, 5.0f);
}

void execute() {
	rsForEach(gScript, gIn, gOut);
}

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {
	float2 coordIn = { x, y };
	float radiusNorm = distance(gCenter, coordIn) / gBmpWidth;

	float4 pixelIn = rsUnpackColor8888(in);

	float darkenFactor = (1 - clamp((radiusNorm - gRadiusInnerNorm) / gRadiusDeltaNorm, 0.0f, 1.0f) * gIntensityNorm * 1.1f);

	pixelIn *= darkenFactor;
	pixelIn[3] = 1;

	return rsPackColorTo8888(pixelIn);
}
