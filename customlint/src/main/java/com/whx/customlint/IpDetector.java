package com.whx.customlint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

public class IpDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create("DirectUseIp",
            "直接使用ipv 4",
            "不利于升级ipv 6",
            Category.MESSAGES,
            9,
            Severity.ERROR,
            new Implementation(IpDetector.class, Scope.CLASS_FILE_SCOPE));



}
