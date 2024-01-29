<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    <!-- header.jsp -->
    <%@ include file="/WEB-INF/view/layout/header.jsp" %>

    <div class="col-sm-8">
    	<h2>회원가입</h2>
    	<h5>어서오세요 환영합니다</h5>
	    <form action="/user/sign-in" method="post" >
		  <div class="form-group">
		    <label for="username">username:</label>
		    <input type="text" name="username" class="form-control" placeholder="Enter username" id="username">
		  </div>
		  <div class="form-group">
		    <label for="pwd">password:</label>
		    <input type="password" name="password" class="form-control" placeholder="Enter password" id="pwd">
		  </div>

		  <button type="submit" class="btn btn-primary">로그인</button>
		</form>
	</div>
	</br>
	</div>

	<!-- footer.jsp -->
	<%@ include file="/WEB-INF/view/layout/footer.jsp" %>