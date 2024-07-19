package controller;

import exception.ModelException;
import exception.RequestException;
import handler.UserHandler;
import util.HttpRequest;
import util.HttpRequestMapper;
import util.HttpResponse;
import util.IOUtil;

/**
 * 회원가입 요청을 처리
 */
public class SignUpController extends AbstractController{

    /**
     * 회원가입 화면을 불러옴
     * @param request
     * @return HttpResponse
     */
    @Override
    public HttpResponse doGet(HttpRequest request) {
        String path = request.getRequestPath();
        return HttpResponse.forward(path, request.getHttpVersion(), IOUtil.readBytesFromFile(path));
    }

    /**
     * 회원가입 요청을 처리
     * @param request
     * @return HttpResponse
     */
    @Override
    public HttpResponse doPost(HttpRequest request) {
        try {
            UserHandler.getInstance().create(request.getBodyMap());
            return HttpResponse.sendRedirect(HttpRequestMapper.DEFAULT_PAGE.getPath(), request.getHttpVersion());
        } catch (RequestException | ModelException e) {
            return HttpResponse.sendRedirect(HttpRequestMapper.SIGNUP.getPath(), request.getHttpVersion());
        }
    }
}
