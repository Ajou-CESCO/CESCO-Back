package com.cesco.pillintime;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;

public class CustomTestWatcher implements TestWatcher, TestExecutionExceptionHandler {

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = context.getDisplayName();
        System.out.println(testName + ": 성공");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = context.getDisplayName();
        System.out.println(testName + ": 실패");
    }

    // 테스트 중 예외 발생 시 여기서 예외를 잡고 다시 던짐.
    // 그로 인해 JUnit이 예외가 발생했다는 것을 인식함.
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        throw throwable;
    }
}