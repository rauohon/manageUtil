package manageUtils;

import java.util.Arrays;

public class caseToCase {
	/**
     * underscore ('_') 가 포함되어 있는 문자열을 Camel Case ( 낙타등
     * 표기법 - 단어의 변경시에 대문자로 시작하는 형태. 시작은 소문자) 로 변환해주는
     * utility 메서드 ('_' 가 나타나지 않고 첫문자가 대문자인 경우도 변환 처리
     * 함.)
     * @param underScore
     *        - '_' 가 포함된 변수명
     * @return Camel 표기법 변수명
     */
    public static void uderScore2CamelCase(String[] motherSource) {

        // '_' 가 나타나지 않으면 이미 camel case 로 가정함.
        // 단 첫째문자가 대문자이면 camel case 변환 (전체를 소문자로) 처리가
        // 필요하다고 가정함. --> 아래 로직을 수행하면 바뀜
    	
    	String underScore = Arrays.toString(motherSource);
    	
        if (underScore.indexOf('_') < 0
            && Character.isLowerCase(underScore.charAt(0))) {
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        int len = underScore.length();

        for (int i = 0; i < len; i++) {
            char currentChar = underScore.charAt(i);
            if (currentChar == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }
        String returnResult = "";
        returnResult = result.toString().replace("[", "");
        returnResult = returnResult.replace("]", "");
        for (String word : returnResult.split(", ")) {
        	System.out.println(word);
        }
    }
    
    public static void uderScore2CamelCase(String underScore) {
    	if (underScore.indexOf('_') < 0
                && Character.isLowerCase(underScore.charAt(0))) {
            }
            StringBuilder result = new StringBuilder();
            boolean nextUpper = false;
            int len = underScore.length();

            for (int i = 0; i < len; i++) {
                char currentChar = underScore.charAt(i);
                if (currentChar == '_') {
                    nextUpper = true;
                } else {
                    if (nextUpper) {
                        result.append(Character.toUpperCase(currentChar));
                        nextUpper = false;
                    } else {
                        result.append(Character.toLowerCase(currentChar));
                    }
                }
            }
            String returnResult = "";
            returnResult = result.toString().replace("[", "");
            returnResult = returnResult.replace("]", "");
            for (String word : returnResult.split(", ")) {
            	System.out.println(word);
            }
    }
    
    /**
	 * camel 스타일을 underScore 스타일로 변환  
	 * userName or UserName => USER_NAME
	 * @param str
	 * @return value
	 */
	public static void camelCase2UnderScore(String str) {

		String regex = "([a-z])([A-Z])";
		
		String replacement = "$1_$2";
		String value = "";        
		
		value = str.replaceAll(regex, replacement).toUpperCase();
		
		for (String word : value.split(", ")) {
			System.out.println(word);
		}
		
	}
	
	/**
	 * camel 스타일을 underScore 스타일로 변환  
	 * userName or UserName => USER_NAME
	 * @param str
	 * @return value
	 */
	public static void camelCase2UnderScore(String[] motherSource) {
		
		String camel = Arrays.toString(motherSource);
		
		String regex = "([a-z])([A-Z])";
		
		String replacement = "$1_$2";
		String value = "";        
		
		value = camel.replaceAll(regex, replacement).toUpperCase();
		
		String returnResult = "";
        
		returnResult = value.toString().replace("[", "");
        returnResult = returnResult.replace("]", "");
		
        for (String word : returnResult.split(", ")) {
			System.out.println(word);
		}
		
	}

}
