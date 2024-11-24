package servletex;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet{
	@Override
	public void init() throws ServletException {
		System.out.println("로그인 서블릿 초기화");
	}
	
	@Override
	public void destroy() {
		System.out.println("로그인 서블릿 소멸");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userid = req.getParameter("userid");
		String userpw = req.getParameter("userpw");
		
		if(userid.equals(userpw)){
			resp.sendRedirect("/main.jsp");
			System.out.println("로그인 성공!");
		} else {
			resp.sendRedirect("/login.jsp");
			System.out.println("로그인 실패!");
		}
	}
}
