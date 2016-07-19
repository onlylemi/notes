# Android 中的图像格式

> 
* A：透明度
* R：红色
* G：绿
* B：蓝

## Bitmap.Config

### `ALPHA_8`

> Each pixel is stored as a single translucency (alpha) channel. This is very useful to efficiently store masks for instance. No color information is stored. With this configuration, each pixel requires 1 byte of memory.

原图的每一个像素以半透明显示。只有透明度，没有颜色，每个像素点占8位，1字节

### `ARGB_4444`

> Each pixel is stored on 2 bytes. The three RGB color channels and the alpha channel (translucency) are stored with a 4 bits precision (16 possible values.) This configuration is mostly useful if the application needs to store translucency information but also needs to save memory. It is recommended to use ARGB_8888 instead of this configuration. Note: as of KITKAT, any bitmap created with this configuration will be created using ARGB_8888 instead.

(已经被遗弃) 即A=4，R=4，G=4，B=4，每个像素点占4+4+4+4=16位，2字节

### `ARGB_8888`

> Each pixel is stored on 4 bytes. Each channel (RGB and alpha for translucency) is stored with 8 bits of precision (256 possible values.) This configuration is very flexible and offers the best quality. It should be used whenever possible.

即A=8，R=8，G=8，B=8，每个像素点占8+8+8+8=32位，4字节

### `RGB_565`

> Each pixel is stored on 2 bytes and only the RGB channels are encoded: red is stored with 5 bits of precision (32 possible values), green is stored with 6 bits of precision (64 possible values) and blue is stored with 5 bits of precision. This configuration can produce slight visual artifacts depending on the configuration of the source. For instance, without dithering, the result might show a greenish tint. To get better results dithering should be applied. This configuration may be useful when using opaque bitmaps that do not require high color fidelity.

即R=5，G=6，B=5，没有透明度，那么一个像素点占5+6+5=16位，两字节

## ImageFormat

### `NV21`

> YCrCb format used for images, which uses the NV21 encoding format.  
This is the default format for Camera preview images, when not otherwise set with setPreviewFormat(int).  
For the android.hardware.camera2 API, the YUV_420_888 format is recommended for YUV output instead.  
Constant Value: 17 (0x00000011)

是一种 YUV420SP 格式，紧跟 Y 平面的是 VU 交替的平面。在 `ImageFormat` 下，一般在 `YUVImage` 中使用

### `YUV_420_888`

> Multi-plane Android YUV 420 format  
This format is a generic YCbCr format, capable of describing any 4:2:0 chroma-subsampled planar or semiplanar buffer (but not fully interleaved), with 8 bits per color sample.  
Images in this format are always represented by three separate buffers of data, one for each color plane. Additional information always accompanies the buffers, describing the row stride and the pixel stride for each plane.

这是一种通用 YCbCr 格式，能够用来描述4：2：0的 planner 或者 semiplanner 的色彩空间，这种格式用三个独立的 buffer 来表示 Y、U、V 色彩空间