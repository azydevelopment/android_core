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
#pragma rs java_package_name(com.noodlesandnaan.renderscript.scripts.RevealRadial)

#pragma rs_fp_relaxed

static rs_script gScript;
static rs_allocation gInFront;
static rs_allocation gInBack;
static rs_allocation gOut;

static int gBmpWidth;
static int gBmpHeight;
static float2 gCenter;

static float gRadiusNorm;

void setIORevealRadial(rs_script script, rs_allocation inFront, rs_allocation inBack, rs_allocation out) {
	gScript = script;
	gInFront = inFront;
	gInBack = inBack;
	gOut = out;

	gBmpWidth = rsAllocationGetDimX(gInFront);
	gBmpHeight = rsAllocationGetDimY(gInFront);
	gCenter[0] = gBmpWidth / 2.0f;
	gCenter[1] = gBmpHeight / 2.0f;
}

void setParamsRevealRadial(float radiusNorm) {
	gRadiusNorm = radiusNorm;
}

void execute() {
	rsForEach(gScript, gInFront, gOut);
}

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {

	float xNorm = (float) x / gBmpWidth;
	float yNorm = (float) y / gBmpHeight;

	float2 pos = { x, y };

	float distFromCenter = distance(pos, gCenter);

	float4 pixelInFront = rsUnpackColor8888(in);
	float4 pixelInBack = rsUnpackColor8888(*(uchar4*) rsGetElementAt(gInBack, x, y));

	float mixFactor = step(distFromCenter / gBmpWidth, gRadiusNorm);
	float4 pixelOut = mix(pixelInFront, pixelInBack, mixFactor);

	return rsPackColorTo8888(pixelOut);
}
