package cn.jiguang.av;

/**
 * 为什么要写这么多文件夹 & 类, 因为if全都写进1个 jiguang.java 里面的话, 虽然编译能通过, 但是实际运行的时候会报错:
 * java.lang.NoClassDefFoundError: Failed resolution of: Lcn/jiguang$api$utils$JCollectionAuth;
 *
 * 包路径和嵌套类路径在编译期看起来一样，但二进制名不同：
 * 写法                           二进制名                                运行时能找到吗
 * package cn.jiguang.api.utils;  cn/jiguang/api/utils/JCollectionAuth  ✅ 和真实 SDK 一致
 * class JCollectionAuth
 *
 * package cn;                    cn/jiguang$api$utils$JCollectionAuth  ❌ 名字对不上
 * public class jiguang {
 *     public static class api {
 *         public static class utils {
 *             public static class JCollectionAuth {
 *                 public static void setAuth(Context var0, boolean var1) {
 *                 }
 *             }
 *         }
 *     }
 * }
 */
public class b {
    public static boolean a() {
        return false;
    }
}
