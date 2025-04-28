package team03.monew.util.exception.article;

import team03.monew.util.exception.ErrorCode;
import team03.monew.util.exception.MonewException;

public class ArticleException extends MonewException {

    public ArticleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
