public class HttpResponse {
    int statusCode;
    String statusText;
    String contentType;
    byte[] body;

    static  HttpResponse ok(byte[] body, String type) {
        HttpResponse r = new HttpResponse();
        r.statusCode = 200;
        r.statusText = "OK";
        r.contentType = type;
        r.body = body;
        return r;
    }

    static HttpResponse notFound() {
        HttpResponse r = new HttpResponse();
        r.statusCode = 404;
        r.statusText = "not Found";
        r.contentType = "text/plain";
        r.body = "not Found".getBytes();
        return r;
    }

    static HttpResponse serverError() {
        HttpResponse r = new HttpResponse();
        r.statusCode = 500;
        r.statusText = "Internal Server Error";
        r.contentType = "text/plain";
        r.body = "Server Error".getBytes();
        return r;
    }
}

