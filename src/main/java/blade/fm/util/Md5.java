package blade.fm.util;

import java.security.MessageDigest;

import blade.kit.log.Logger;

public class Md5 {

	/**
	 * log4j
	 */
	public static final Logger log4j = Logger.getLogger(Md5.class);

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String createMd5(String inputString) {
		return encodeByMD5(inputString);
	}

	/**
	 * @param password
	 * @param inputString
	 * @return
	 */
	public static boolean authenticatePassword(String password,
			String inputString) {
		if (password.equals(encodeByMD5(inputString))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param originString
	 * @return
	 */
	private static String encodeByMD5(String originString) {
		if (originString != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");

				byte[] results = md.digest(originString.getBytes());

				String resultString = byteArrayToHexString(results);
				return resultString;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param b
	 * @return
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();

		// System.out.println("16-------------");

		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * @param b
	 * @return
	 */
	private static String byteToHexString(byte b) {
		// System.out.println(b);

		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 将32字节的md5转换成16字节
	 * 
	 * @return
	 */
	public static byte[] getMd5Original(String md5Pass) {
		if (!"".equals(md5Pass)) {
			byte[] result = new byte[16];
			for (int i = 0; i < 32; i = i + 2) {
				String hexHigh = md5Pass.substring(i, i + 1);
				String hexLow = md5Pass.substring(i + 1, i + 2);

				int total = Integer.parseInt(hexHigh, 16) * 16
						+ Integer.parseInt(hexLow, 16);

				// String binaryHigh =
				// UdpUtil.lPad(Integer.toBinaryString(Integer.parseInt(hexHigh,16)),
				// 4, "0");
				// String binaryLow =
				// UdpUtil.lPad(Integer.toBinaryString(Integer.parseInt(hexLow,16)),
				// 4, "0");
				// String totalBinary = binaryHigh + binaryLow;
				// int total = Integer.parseInt(totalBinary, 2);

				result[i / 2] = (byte) total;
			}

			// for(byte temp : result){
			// System.out.println(temp);
			// }
			return result;
		}
		return md5Pass.getBytes();
	}

	public static void main(String[] args) {
		String md1 = createMd5("zhangyifei@feitan.net123123");
		System.out.println(md1);

		
		
	}
}
