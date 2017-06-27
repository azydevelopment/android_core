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
#pragma rs java_package_name(com.noodlesandnaan.renderscript.scripts.ColorMod)

#pragma rs_fp_relaxed

static rs_script gScript;
static rs_allocation gIn;
static rs_allocation gOut;

static int gBmpWidth;
static int gBmpHeight;
static float2 gCenter;

float* gMatColor;

void setIOColorMod(rs_script script, rs_allocation in, rs_allocation out) {
	gScript = script;
	gIn = in;
	gOut = out;

	gBmpWidth = rsAllocationGetDimX(gIn);
	gBmpHeight = rsAllocationGetDimY(gIn);
	gCenter[0] = gBmpWidth / 2.0f;
	gCenter[1] = gBmpHeight / 2.0f;
}

void execute() {
	rsForEach(gScript, gIn, gOut);
}

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {
	float4 pixelIn = rsUnpackColor8888(in);

	float4 pixelOut;

	for (int i = 0; i < 4; i++) {
		float c0 = gMatColor[i * 5];
		float c1 = gMatColor[i * 5 + 1];
		float c2 = gMatColor[i * 5 + 2];
		float c3 = gMatColor[i * 5 + 3];
		float c4 = gMatColor[i * 5 + 4];

		pixelOut[i] = c0 * pixelIn[0] + c1 * pixelIn[1] + c2 * pixelIn[2] + c3 * pixelIn[3] + c4;
	}

	return rsPackColorTo8888(pixelOut);
}
