package iminto.collection;
import java.util.BitSet;
/**
 * 布隆过滤器
 * Desc:布隆过滤器可以用于检索一个元素是否在一个集合中
 * Author:waitfox@qq.com
 * Date:2012-11-19 下午8:05:51
 */
public class BloomFilter {
	//DEFAULT_SIZE为2的25次方
    private static final int DEFAULT_SIZE = 2 << 24;
    /* 不同哈希函数的种子，一般应取质数,seeds数据共有7个值，则代表采用7种不同的HASH算法 */
    private static final int[] seeds = new int[] { 5, 7, 11, 13, 31, 37, 61 };
    //BitSet实际是由“二进制位”构成的一个Vector。假如希望高效率地保存大量“开－关”信息，就应使用BitSet.
    //BitSet的最小长度是一个长整数（Long）的长度：64位
    private BitSet bits = new BitSet(DEFAULT_SIZE);
    /* 哈希函数对象 */
    private SimpleHash[] func = new SimpleHash[seeds.length];

    //构造函数
    public BloomFilter() {
       for (int i = 0; i < seeds.length; i++) {
           //给出所有的hash值，共计seeds.length个hash值。共7位。
           //通过调用SimpleHash.hash(),可以得到根据7种hash函数计算得出的hash值。
           //传入DEFAULT_SIZE(最终字符串的长度），seeds[i](一个指定的质数)即可得到需要的那个hash值的位置。
           func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
       }
    }

    // 将字符串标记到bits中，即设置字符串的7个hash值函数为1
    public void add(String value) {
       for (SimpleHash f : func) {
           bits.set(f.hash(value), true);
       }
    }

    //判断字符串是否已经被bits标记
    public boolean contains(String value) {
       //确保传入的不是空值
       if (value == null) {
           return false;
       }
       boolean ret = true;
       //计算7种hash算法下各自对应的hash值，并判断
       for (SimpleHash f : func) {
           //&&是boolen运算符，只要有一个为0，则为0。即需要所有的位都为1，才代表包含在里面。
           //f.hash(value)返回hash对应的位数值
           //bits.get函数返回bitset中对应position的值。即返回hash值是否为0或1。
           ret = ret && bits.get(f.hash(value));
       }
       return ret;
    }

    /* 哈希函数类 */

    public static class SimpleHash {
       //cap为DEFAULT_SIZE的值，即用于结果的最大的字符串长度。
       //seed为计算hash值的一个给定key，具体对应上面定义的seeds数组
       private int cap;
       private int seed;

       public SimpleHash(int cap, int seed) {
           this.cap = cap;
           this.seed = seed;
       }

       //计算hash值的具体算法,hash函数，采用简单的加权和hash
       public int hash(String value) {
           //int的范围最大是2的31次方减1，或超过值则用负数来表示
           int result = 0;
           int len = value.length();
           for (int i = 0; i < len; i++) {
              //数字和字符串相加，字符串转换成为ASCII码
              result = seed * result + value.charAt(i);
              //System.out.println(result+"--"+seed+"*"+result+"+"+value.charAt(i));
           }
       //  System.out.println("result="+result+";"+((cap - 1) & result));
       //  System.out.println(414356308*61+'h');  执行此运算结果为负数，为什么？
           //&是java中的位逻辑运算，用于过滤负数（负数与进算转换成反码进行）。
           return (cap - 1) & result;
       }
    }
}
