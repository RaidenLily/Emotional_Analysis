package com;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        String a="对于当下形势，习近平称中国疫情防控取得重大战略成果，经济发展呈现稳定转好态势，在疫情防控和经济恢复上都走在世界前列。习近平提出，要增强信心、迎难而上，把疫情造成的损失补回来，争取全年经济发展好成绩。";
        Split.doAll(a);
    }
}