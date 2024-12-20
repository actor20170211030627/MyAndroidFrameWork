package com.actor.sample.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.actor.myandroidframework.utils.glide.MyImageViewTarget;
import com.actor.myandroidframework.utils.glide.MyRequestListener;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.actor.sample.R;
import com.actor.sample.adapter.GlideExampleAdapter;
import com.actor.sample.databinding.ActivityGlideExampleBinding;
import com.actor.sample.utils.Global;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.prefill.PreFillType;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Description: 主页 -> Glide使用
 * Author     : ldf
 * Date       : 2020/2/4 on 13:06
 *
 * https://github.com/bumptech/glide
 * 官方中文文档: https://muyangmin.github.io/glide-docs-cn/
 * 中文文档:     http://mrfu.me/2016/02/27/Glide_Getting_Started/
 */
public class GlideExampleActivity extends BaseActivity<ActivityGlideExampleBinding> {

    private              int            dp3;
    private              RequestOptions requestOptions;

    private MyImageViewTarget imageViewTarget;
    private final MyRequestListener requestListener = new MyRequestListener(2, new Animatable2Compat.AnimationCallback() {
        @Override
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            viewBinding.ivTreasureBox1.setImageResource(R.drawable.icon_treasure_box_opened);//设置最后1帧
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页 -> Glide测试");
        dp3 = Global.DP1 * 3;

        viewBinding.recyclerView.addItemDecoration(new BaseItemDecoration(10, 10));
        viewBinding.recyclerView.setAdapter(new GlideExampleAdapter(this));



        //Glide播放次数初始化
        imageViewTarget = new MyImageViewTarget(viewBinding.ivTreasureBox0, 2, new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                viewBinding.ivTreasureBox0.setImageResource(R.drawable.icon_treasure_box_opened);
            }
        });
        viewBinding.stvPlay0.setOnClickListener(v -> {
            Glide.with(this).asGif()
                    .skipMemoryCache(true) //防止Gif重复播放时, 会先显示最后1帧的图片
                    .placeholder(R.drawable.icon_treasure_box_closed) //第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
                    .load(R.drawable.gif_treasure_box_right)
                    .into(imageViewTarget);
        });
        viewBinding.stvPlay1.setOnClickListener(v -> {
            Glide.with(this).asGif()
                    .skipMemoryCache(true) //防止Gif重复播放时, 会先显示最后1帧的图片
                    .placeholder(R.drawable.icon_treasure_box_closed) //第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
                    .load(R.drawable.gif_treasure_box_right)
                    .listener(requestListener)
                    .into(viewBinding.ivTreasureBox1);
        });



        /**
         * 其余方法, 见:
         * @see #glideGet()
         * @see #glideOthers()
         * @see #glideWithOther()
         * @see #glideWith(ImageView)
         */
    }

    //1. Glide.get
    private void glideGet() {
        Glide.get(this).clearDiskCache();//清除磁盘缓存(必须在子线程中调用,否则报错)
        Glide.get(this).clearMemory();
        ArrayPool arrayPool = Glide.get(this).getArrayPool();
        BitmapPool bitmapPool = Glide.get(this).getBitmapPool();
        Context context = Glide.get(this).getContext();
        Registry registry = Glide.get(this).getRegistry();
        RequestManagerRetriever requestManagerRetriever = Glide.get(this).getRequestManagerRetriever();
        Glide.get(this).onConfigurationChanged(new Configuration());
        Glide.get(this).onLowMemory();
        Glide.get(this).onTrimMemory(android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND);//int level
        Glide.get(this).preFillBitmapPool(new PreFillType.Builder(1, 1));//size/ width, height
        MemoryCategory memoryCategory = Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        Glide.get(this).trimMemory(android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND);//int level
    }

    //2. Glide.other
    private void glideOthers() {
        File photoCacheDir = Glide.getPhotoCacheDir(this);
        File cacheName = Glide.getPhotoCacheDir(this, "cacheName");
        Glide.init(Glide.get(this));
        Glide.init(this, new GlideBuilder()/*...*/);
        Glide.tearDown();
    }

    //3. Glide.with 其它方法
    private void glideWithOther() {
        RequestManager requestManager = Glide.with(this);
//                .addDefaultRequestListener(RequestListener)
//                .applyDefaultRequestOptions(RequestOptions)
        Glide.with(this).clear(viewBinding.ivTest/*Target*/);//取消加载
        RequestBuilder<File> download = Glide.with(this).download(Global.BAIDU_LOGO);//Object model
        RequestBuilder<File> downloadOnly = Glide.with(this).downloadOnly();
        boolean paused = Glide.with(this).isPaused();
        Glide.with(this).onDestroy();
        Glide.with(this).onStart();
        Glide.with(this).onStop();
        Glide.with(this).pauseAllRequests();
        Glide.with(this).pauseRequests();
        Glide.with(this).pauseRequestsRecursive();
        Glide.with(this).resumeRequests();
        Glide.with(this).resumeRequestsRecursive();
        requestManager = Glide.with(this).setDefaultRequestOptions(requestOptions);
        String s = Glide.with(this).toString();
    }

    //3. Glide.with
    private void glideWith(ImageView iv) {
        //如果 url 为 null，Glide 会清空 View 的内容，或者显示 placeholder/fallback
        Glide.with(this).load((String) null).into(iv);

        //RequestOptions extends BaseRequestOptions, 请求选项
        //1. 单项配置
        requestOptions = RequestOptions.bitmapTransform((Transformation<Bitmap>) null);
        requestOptions = RequestOptions.centerCropTransform();
        requestOptions = RequestOptions.centerInsideTransform();
        requestOptions = RequestOptions.circleCropTransform();
        requestOptions = RequestOptions.decodeTypeOf((Class<?>) null);
        requestOptions = RequestOptions.diskCacheStrategyOf((DiskCacheStrategy) null);
        requestOptions = RequestOptions.downsampleOf((DownsampleStrategy) null);
        requestOptions = RequestOptions.encodeFormatOf((Bitmap.CompressFormat) null);
        requestOptions = RequestOptions.encodeQualityOf(100);//int quality
        requestOptions = RequestOptions.errorOf(R.mipmap.ic_launcher);
        requestOptions = RequestOptions.errorOf((Drawable) null);
        requestOptions = RequestOptions.fitCenterTransform();
        requestOptions = RequestOptions.formatOf((DecodeFormat) null);
        requestOptions = RequestOptions.frameOf(1_000_000L);//加载第1秒的帧数作为封面:1_000_000L
        requestOptions = RequestOptions.noAnimation();
        requestOptions = RequestOptions.noTransformation();
        requestOptions = RequestOptions.option((Option/*<T>*/) null, (Object/*T*/) null);
        requestOptions = RequestOptions.overrideOf(200);//int size, 宽 = 高
        requestOptions = RequestOptions.overrideOf(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);//ORIGINAL:原始
        requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher);
        requestOptions = RequestOptions.placeholderOf((Drawable) null);
        requestOptions = RequestOptions.priorityOf((Priority) null);
        requestOptions = RequestOptions.signatureOf((Key) null);
        requestOptions = RequestOptions.sizeMultiplierOf(1F);//float sizeMultiplier, 大小倍数
        requestOptions = RequestOptions.skipMemoryCacheOf(false);
        requestOptions = RequestOptions.timeoutOf(0);//ms

        //2. 多项配置, 加载视频示例(transform, error, placeholder是父类的方法)
        requestOptions = RequestOptions.frameOf(1_000_000L)
                .transform(new CenterCrop(), new RoundedCorners(dp3))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher);
                /*...*/

        //RequestBuilder 也可以被复用于开始多个请求
        Glide.with(this)
//                .as(Class<ResourceType> resourceClass)
//                .asBitmap()
                .asDrawable()   //默认
//                .asFile()
//                .asGif()      //如果不是gif, 不能加载出来, 会一片空白
                .load(Global.BAIDU_LOGO)
//                .getDiskCacheStrategy()   //DiskCacheStrategy, 获取磁盘缓存策略
//                .getErrorId()
//                .getErrorPlaceholder()    //Drawable
//                .getFallbackId()
//                .getFallbackDrawable()    //Drawable, 返回.fallback设置的值
//                .getOnlyRetrieveFromCache()//boolean
//                .getOptions()//Options
//                .getOverrideHeight()      //int
//                .getOverrideWidth()       //int
//                .getPlaceholderId()
//                .getPlaceholderDrawable() //Drawable, 返回占位图
                /*...*/

                .addListener((RequestListener<Drawable>) null)//add添加监听
                .apply(requestOptions)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//既缓存原始图片，也缓存转换过后的图片
//                                   DiskCacheStrategy.AUTOMATIC: 根据图片资源智能选择使用哪一种缓存策略（默认）
//                                   DiskCacheStrategy.DATA: 只缓存原始图片(转换前)
//                                   DiskCacheStrategy.NONE: 不设置缓存
//                                   DiskCacheStrategy.SOURCE: 只缓存转换过后的图片(转换后)
                .error(R.mipmap.ic_launcher)    //指定图片加载失败显示的图片,可以不设置(什么都不显示)
                .fallback(R.mipmap.ic_launcher) //请求的url/model为 null 时展示
//                .format((DecodeFormat) null)
//                .frame(1_000_000L)
                .listener(new RequestListener<Drawable>() {//set设置监听
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                /*...*/

//                .preload(/*int width, int height*/)//预加载, 空参 & 有参
//                .submit(/*int width, int height*/)//下载图片, 返回FutureTarget,File file = FutureTarget.get();//同步方法
//                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//300, 300 指定图片的尺寸(可不设置)
//                .placeholder(R.mipmap.ic_launcher/*color*/)//占位符, 指定图片未成功加载前显示的图片或颜色,可以不设置(什么都不显示)
//                .priority(Priority.HIGH)//优先级?
//                .dontTransform()    //加载的图片不变形(可在RecyclerView中设置)
//                .dontAnimate()  //加载时不闪动
//                .skipMemoryCache(false)// 跳过内存缓存

                //过渡选项, 决定你的加载完成时会发生什么(TransitionOptions)
                .transition(DrawableTransitionOptions.withCrossFade())//淡入淡出

                //略缩图
                .thumbnail(1F)//float sizeMultiplier
                .thumbnail(Glide.with(this).load("thumbnailUrl"))

//        //变换, extends BitmapTransformation implements Transformation extends Key
                .centerCrop()   //CenterCrop extends BitmapTransformation implements Transformation 中心裁剪
                .centerInside() //CenterCrop extends BitmapTransformation implements Transformation
                .circleCrop()   //CircleCrop extends BitmapTransformation implements Transformation 圆形图片, CENTER_INSIDE
                .fitCenter()    //CenterCrop extends BitmapTransformation implements Transformation
                .optionalCircleCrop()//CircleCrop extends BitmapTransformation implements Transformation 圆形图片, CENTER_OUTSIDE

                .transform(new CenterCrop())
                .transform(new CenterInside())
                .transform(new CircleCrop())//圆形
                .transform(new FitCenter())
                .transform(new Rotate(90))  //旋转
                .transform(new RoundedCorners(dp3))//圆角
                .transform(new CenterCrop(), new RoundedCorners(dp3))//xml中CenterCrop和RoundedCorners冲突,导致没有圆角. 把CenterCrop写在前面
//                .transition(new GlideRoundTransform(this, 15))//自定义圆角

//                .transform(new BitmapDrawableTransformation(Transformation<Bitmap> wrapped))//implements Transformation 过时
//                .transform(new MultiTransformation(Transformation...))//implements Transformation, 多重变换
//                .transform(new GifDrawableTransformation(Transformation<Bitmap> wrapped))//implements Transformation
//                .transform(new UnitTransformation())//implements Transformation

                //目标
//                .into(new SimpleTarget<GifDrawable>() {//asGif, target, 已封装成MySimpleTarget
//                    @Override
//                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
//                        if (ivGiftShow != null) {
//                            resource.setLoopCount(2);//gif图循环播放次数
//                            ivGiftShow.setImageDrawable(resource);
//                            resource.start();
//                        }
//                    }
//
//                    @Override
//                    public void onStop() {//手动重写onStop方法
//                        super.onStop();
//                        ivGiftShow.setImageDrawable(null);
//                    }
//                })
//                .into(new SimpleTarget<Bitmap>() {//asBitmap, 转Bitmap
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                    }
//                })
                //RecyclerView尺寸不同时, 复用问题
//                .into(new DrawableImageViewTarget(iv, /*waitForLayout=*/ true))
                .into(iv)//指定显示图片的ImageView
                ;
    }
}
