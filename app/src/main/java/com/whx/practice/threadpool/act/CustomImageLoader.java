package com.whx.practice.threadpool.act;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.whx.practice.MyApplication;

import java.io.File;

/**
 *
 * 图片加载类：builder模式封装图片加载过程，便于后期换图片库时不影响业务代码
 */

public final class CustomImageLoader {

    private static final String TAG = CustomImageLoader.class.getSimpleName();

    private static final int DEFAULT_RESIZE_SIZE = 500;

    private final Builder builder;

    public static Object getContextTag(Context context) {
        return context != null ? String.valueOf(context.hashCode()) : null;
    }

    public static void cancelTag(Context context) {
        if (context == null) {
            return;
        }
        Picasso.with(context).cancelTag(getContextTag(context));
    }

    private CustomImageLoader(@NonNull Builder builder) {
        this.builder = builder;
    }

    /**
     * 加载图片到ImageView上
     * 会使用fit()方法自动裁剪
     *
     * @param callback 加载状态回调，不需要时传null即可
     */
    public void loadImage(ImageView imageView, @Nullable Callback callback) {
        loadImage(imageView, callback, 0, 0);
    }

    /**
     * 加载图片到ImageView上
     * 如果resize大小有效,则不会使用fit()方法自动裁剪
     *
     * @param callback     加载状态回调，不需要时传null即可
     * @param resizeWidth  裁剪宽度,传的话必须为正数,否则会根据ImageView的宽高裁剪
     * @param resizeHeight 裁剪高度,传的话必须为正数,否则会根据ImageView的宽高裁剪
     * @param imageView    一定要注意,如果不手动resize-通过ImageView大小来自动裁剪,那么在ImageView一定不能为GONE状态,否则会导致不被测量,w/h都为0,无法自动裁剪(picasso和glide等都有这问题)
     */
    public void loadImage(ImageView imageView, @Nullable final Callback callback, int resizeWidth, int resizeHeight) {
        //构建请求加载
        RequestCreator creator = buildCreator();
        if (creator == null || imageView == null) {//请求构建失败
            if (builder.errorId > 0 && imageView != null) {//只展示错误占位图进行提示
                imageView.setImageResource(builder.placeHolderId);
            }
            return;
        }
        //resize
        if (resizeWidth > 0 && resizeHeight > 0) {//resize优先
            creator.resize(resizeWidth, resizeHeight);
        } else {//通过fit裁剪
            creator.fit();
        }
        //load
        if (callback != null) {
            creator.into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onError() {
                    callback.onError();
                }
            });
        } else {
            creator.into(imageView);
        }
    }

    /**
     * 加载图片到Target回调上(也可不设置回调),一定要进行裁剪设置
     *
     * @param resizeWidth  裁剪宽度,必须为正数
     * @param resizeHeight 裁剪高度,必须为正数
     */
    public void loadImage(@Nullable final Target target, int resizeWidth, int resizeHeight) {
        //构建请求加载
        RequestCreator creator = buildCreator();
        if (creator == null) {//请求构建失败
            return;
        }
        //必须要resize防止OOM
        if (resizeWidth > 0 && resizeHeight > 0) {
            creator.resize(resizeWidth, resizeHeight);
        } else {//设置默认resize大小
            creator.resize(DEFAULT_RESIZE_SIZE, DEFAULT_RESIZE_SIZE);
            Log.i(TAG, "don't set correct resize size,use the DEFAULT_RESIZE_SIZE(720,720) to resize");
        }
        //load
        if (target != null) {
            creator.into(new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    target.onImageLoaded(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    target.onImageFailed(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    target.onImageStartLoad(placeHolderDrawable);
                }
            });
        } else {//只是拉取图片并不使用
            creator.fetch();
        }
    }

    private RequestCreator buildCreator() {
        Context appContext = MyApplication.getAppContext();
        if (appContext == null || builder.url == null || TextUtils.isEmpty(builder.url.trim())) {//非法url不进行加载
            return null;
        }
        //构建请求加载
        RequestCreator creator = Picasso.with(appContext).load(builder.url).centerInside().noFade();
        //占位图
        if (builder.placeHolderId > 0) {
            creator.placeholder(builder.placeHolderId);
        }
        if (builder.errorId > 0) {
            creator.error(builder.errorId);
        }
        //memory policy
        buildMemoryStrategy(builder.memoryStrategies, creator);
        //network policy
        buildNetworkStrategy(builder.networkStrategies, creator);
        //transformation
        buildTransformations(builder.transformations, creator);
        //tag
        if (builder.tag != null) {
            creator.tag(builder.tag);
        }
        return creator;
    }

    private void buildTransformations(Transformation[] transformations, @NonNull RequestCreator creator) {
        if (transformations != null && transformations.length > 0) {
            for (final Transformation transformation : transformations) {
                if (transformation != null) {
                    creator.transform(new com.squareup.picasso.Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            return transformation.transform(source);
                        }

                        @Override
                        public String key() {
                            return transformation.getKey();
                        }
                    });
                }
            }
        }
    }

    private void buildMemoryStrategy(MemoryStrategy[] strategies, @NonNull RequestCreator creator) {
        int len;
        if (strategies != null && (len = strategies.length) > 0) {
            MemoryPolicy[] policies = new MemoryPolicy[len];
            for (int i = 0; i < len; i++) {
                policies[i] = strategies[i].memoryPolicy;
            }
            creator.memoryPolicy(policies[0], policies);
        }
    }

    private void buildNetworkStrategy(NetworkStrategy[] strategies, @NonNull RequestCreator creator) {
        int len;
        if (strategies != null && (len = strategies.length) > 0) {
            NetworkPolicy[] policies = new NetworkPolicy[len];
            for (int i = 0; i < len; i++) {
                policies[i] = strategies[i].networkPolicy;
            }
            creator.networkPolicy(policies[0], policies);
        }
    }

    public interface Callback {

        void onSuccess();

        void onError();
    }

    public interface Target {

        void onImageStartLoad(@Nullable Drawable placeHolder);

        void onImageLoaded(Bitmap bitmap);

        void onImageFailed(@Nullable Drawable errorDrawable);
    }

    public interface Transformation {

        Bitmap transform(Bitmap source);

        /**
         * 作为cache缓存图片的key的一部分,不同transformation对象应该返回不一样的key
         */
        String getKey();
    }

    public enum MemoryStrategy {
        SKIP_MEMORY_READ(MemoryPolicy.NO_CACHE),
        SKIP_MEMORY_WRITE(MemoryPolicy.NO_STORE);

        private final MemoryPolicy memoryPolicy;

        MemoryStrategy(MemoryPolicy memoryPolicy) {
            this.memoryPolicy = memoryPolicy;
        }
    }

    public enum NetworkStrategy {
        SKIP_DISK_READ(NetworkPolicy.NO_CACHE),
        SKIP_DISK_WRITE(NetworkPolicy.NO_STORE),
        SKIP_NETWORK(NetworkPolicy.OFFLINE);

        private final NetworkPolicy networkPolicy;

        NetworkStrategy(NetworkPolicy networkPolicy) {
            this.networkPolicy = networkPolicy;
        }
    }

    public final static class Builder {

        //必要参数
        private final String url;
        private final Object tag;

        private int placeHolderId;
        private int errorId;

        private MemoryStrategy[] memoryStrategies;
        private NetworkStrategy[] networkStrategies;

        private Transformation[] transformations;

        /**
         * 加载file
         */
        public Builder(File file, Context tagContext) {
            this(file != null ? Uri.fromFile(file) : null, tagContext);
        }

        /**
         * 加载uri
         */
        public Builder(Uri uri, Context tagContext) {
            this(uri != null ? uri.toString() : null, tagContext);
        }

        /**
         * 加载url
         * 以context,通常为activity
         */
        public Builder(String url, Context tagContext) {
            this.url = url;
            this.tag = getContextTag(tagContext);
        }

        /**
         * 自定义的bitmap转换
         */
        public Builder transform(Transformation... transformations) {
            this.transformations = transformations;
            return this;
        }

        /**
         * 占位图resId
         */
        public Builder placeHolder(@DrawableRes int placeHolderId) {
            this.placeHolderId = placeHolderId;
            return this;
        }

        /**
         * 加载失败时的占位图resId
         */
        public Builder errorHolder(@DrawableRes int errorId) {
            this.errorId = errorId;
            return this;
        }

        /**
         * 内存策略
         */
        public Builder memoryStrategy(MemoryStrategy... memoryStrategies) {
            this.memoryStrategies = memoryStrategies;
            return this;
        }

        /**
         * 磁盘策略
         */
        public Builder networkStrategy(NetworkStrategy... networkStrategies) {
            this.networkStrategies = networkStrategies;
            return this;
        }

        public CustomImageLoader build() {
            return new CustomImageLoader(this);
        }
    }
}