//package com.dudu;
//
//import org.springframework.stereotype.Controller;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Controller
//public class ExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    public ErrorMessage handleError(HttpServletRequest req, Exception ex) {
//
//        ex.getMessage();
//    }
//
//    class ErrorMessage {
//        public String getReason() {
//            return reason;
//        }
//
//        public void setReason(String reason) {
//            this.reason = reason;
//        }
//
//        private String reason;
//    }
//}
