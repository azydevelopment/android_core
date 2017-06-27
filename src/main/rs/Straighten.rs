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
#pragma rs java_package_name(com.noodlesandnaan.renderscript.scripts.Straighten)

#pragma rs_fp_relaxed

static rs_script gScript;
static rs_allocation gIn;
static rs_allocation gOut;

static int gBmpWidth;
static int gBmpHeight;
static float2 gCenter;

static rs_matrix4x4 gMatTransform;

void setIOStraighten(rs_script script, rs_allocation in, rs_allocation out) {
	gScript = script;
	gIn = in;
	gOut = out;

	gBmpWidth = rsAllocationGetDimX(gIn);
	gBmpHeight = rsAllocationGetDimY(gIn);
	gCenter[0] = gBmpWidth / 2.0f;
	gCenter[1] = gBmpHeight / 2.0f;
}

void setParamsStraighten(float angle) {
	rsMatrixLoadIdentity(&gMatTransform);

	rsMatrixTranslate(&gMatTransform, gCenter[0], gCenter[1], 0);
	rsMatrixRotate(&gMatTransform, angle, 0, 0, 1);

	float arcTan = atan((float) (gBmpHeight / gBmpWidth));
	float len1 = gCenter[0] / cos(arcTan - radians(fabs(angle)));
	float len2 = sqrt(pow(gCenter[0],2) + pow(gCenter[1],2));
	float scale = len1 / len2;
	rsMatrixScale(&gMatTransform, scale, scale, 1);

	rsMatrixTranslate(&gMatTransform, -gCenter[0], -gCenter[1], 0);
}

void execute() {
	rsForEach(gScript, gIn, gOut);
}

uchar4 __attribute__((kernel)) root(uchar4 pixelIn, uint32_t x, uint32_t y) {
	float4 coordIn = { x, y, 0, 1 };

	float4 coordTransformed = rsMatrixMultiply(&gMatTransform, coordIn);

	int xTransformed = clamp(coordTransformed.x, 0.0f, gBmpWidth - 1.0f);
	int yTransformed = clamp(coordTransformed.y, 0.0f, gBmpHeight - 1.0f);

	uchar4* pixelInTransformed = (uchar4*) rsGetElementAt(gIn, xTransformed, yTransformed);

	return *pixelInTransformed;
}

