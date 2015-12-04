package com.lilsmile.testplatformandroid.app;

/**
 * Created by Smile on 21.11.15.
 */
public interface Constants {

    //urls

    String LOGIN_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/auth/login";
    String SIGNUP_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/auth/signup";
    String ANONYMOUS_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/auth/anonymous";
    String LOGOUT_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/auth/logout";
    String GET_TESTS_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/tests";
    String GET_TEST_BY_ID_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/tests/test?id=";
    String PASSED_TEST_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/tests/passed_test";
    String PASSED_CREATED_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/cabinet/passed_created";
    String PASSED_TESTS_STRING = "http://platform.lilsmile.xyz:8080/TestPlatform/rest/cabinet/passed_tests";

    //codes

    String ANONYMOUS = "anonymous";

    String TEST_ID = "test_id";
    String AUTHOR = "author";
    String TEST_CATEGORY = "test_category";
    String TITLE = "title";
    String DESCRIPTION = "description";
    String DATE = "date";
    String QUESTIONS = "questions";
    String ANSWERS = "answers";
    String ANSWER = "answer";
    String NUMBER = "number";
    String WEIGHT = "weight";
    String TOKEN = "token";
    String TYPE = "type";
    String ANSWERS_ARR="answersArr";
    String TOTAL_WEIGHT="totalWeight";
    String USER_WEIGHT="userWeight";
    String CREATED = "created";
    String PASSED = "passed";
    String COUNT = "count";
    String TESTS = "tests";


    //codes for signup
    String OK="200";
    String BAD_LOGIN="205";
    String BAD_EMAIL="210";
    //codes for login
    String WRONG_PASSWORD="250";
    String WRONG_LOGIN="255";

    String LOGIN = "userName";
    String PASSWORD = "passHash";
    String EMAIL = "email";

    String RESULT="result";

    //errors
    String SMTH_IS_WRONG = "100";
    String WRONG_TOKEN = "155";
}
