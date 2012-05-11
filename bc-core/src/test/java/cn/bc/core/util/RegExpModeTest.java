/**
 * 
 */
package cn.bc.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 正则表达式量词测试：贪婪量词、惰性量词、支配量词
 * <table>
 * <tr>
 * <td>贪婪量词</td>
 * <td>惰性量词</td>
 * <td>支配量词</td>
 * <td>描述</td>
 * </tr>
 * <tr>
 * <td>?</td>
 * <td>??</td>
 * <td>?+</td>
 * <td>可以出现0次或1次，但至多出现1次</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>*?</td>
 * <td>*+</td>
 * <td>可以出现任意次，也可以不出现</td>
 * </tr>
 * <tr>
 * <td>+</td>
 * <td>+?</td>
 * <td>++</td>
 * <td>出现1次或多次，但至少出现1次</td>
 * </tr>
 * <tr>
 * <td>{n}</td>
 * <td>{n}?</td>
 * <td>{n}+</td>
 * <td>一定出现n次</td>
 * </tr>
 * <tr>
 * <td>{n,m}</td>
 * <td>{n,m}?</td>
 * <td>{n,m}+</td>
 * <td>至少出现n次，但至多不能超过m次</td>
 * </tr>
 * <tr>
 * <td>{n,}</td>
 * <td>{n,}?</td>
 * <td>{n,}+</td>
 * <td>可以出现任意次，但至少出现n次</td>
 * </tr>
 * </table>
 * 
 * @author dragon
 * 
 */
public class RegExpModeTest {

	/**
	 * 贪婪量词匹配
	 * <p>
	 * 先看整个字符串是不是一个匹配。如果没有发现匹配，它去掉最后字符串中的最后一个字符，并再次尝试。如果还是没有发现匹配，那么
	 * 再次去掉最后一个字符串，这个过程会一直重复直到发现一个匹配或者字符串不剩任何字符。简单量词都是贪婪量词。
	 * </p>
	 * 贪婪量词的工作过程可以这样表示：
	 * <ul>
	 * <li>a)abbbaabbbaaabbb1234</li>
	 * <li>b)abbbaabbbaaabbb123</li>
	 * <li>c)abbbaabbbaaabbb12</li>
	 * <li>d)abbbaabbbaaabbb1</li>
	 * <li>e)abbbaabbbaaabbb //true</li>
	 * </ul>
	 * 可以看到，贪婪量词在取得一次匹配后就会停止工作
	 * 
	 * @throws Exception
	 */
	@Test
	public void test01TanLan() throws Exception {
		String source = "abbbaabbbaaabbb1234";
		String regex = ".*bbb";// 贪婪量词匹配
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		List<String> groups = new ArrayList<String>();
		while (m.find()) {
			groups.add(m.group());
		}
		Assert.assertEquals(1, groups.size());
		Assert.assertEquals("abbbaabbbaaabbb", groups.get(0));
	}

	/**
	 * 惰性量词匹配
	 * <p>
	 * 先看字符串中的第一个字母是不是一个匹配，如果单独着一个字符还不够，就读入下一个字符，组成两个字符的字符串。如果还没有发现匹配，
	 * 惰性量词继续从字符串中添加字符直到发现一个匹配或者整个字符串都检查过也没有匹配。惰性量词和贪婪量词的工作方式恰好相反。
	 * </p>
	 * 惰性量词的工作过程可以这样表示：
	 * <ul>
	 * <li>a)a</li>
	 * <li>b)ab</li>
	 * <li>c)abb</li>
	 * <li>d)abbb //保存结果，并从下一个位置重新开始</li>
	 * 
	 * <li>e)a</li>
	 * <li>f)aa</li>
	 * <li>g)aab</li>
	 * <li>h)aabb</li>
	 * <li>i)aabbb //保存结果，并从下一个位置重新开始</li>
	 * 
	 * <li>j)a</li>
	 * <li>k)aa</li>
	 * <li>l)aaa</li>
	 * <li>m)aaab</li>
	 * <li>n)aaabb</li>
	 * <li>o)aaabbb //保存结果，并从下一个位置重新开始</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test02DuoXing() throws Exception {
		String source = "abbbaabbbaaabbb1234";
		String regex = ".*?bbb";// 惰性量词匹配
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		List<String> groups = new ArrayList<String>();
		while (m.find()) {
			groups.add(m.group());
		}
		Assert.assertEquals(3, groups.size());
		Assert.assertEquals("abbb", groups.get(0));
		Assert.assertEquals("aabbb", groups.get(1));
		Assert.assertEquals("aaabbb", groups.get(2));
	}

	/**
	 * 支配量词匹配
	 * <p>
	 * 只尝试匹配整个字符串。如果整个字符串不能产生匹配，不做进一步尝试。
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void test03ZhiPei() throws Exception {
		String source = "abbbaabbbaaabbb1234";
		String regex = ".*+bbb";// 支配量词匹配
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		Assert.assertFalse(m.find());
	}
}
