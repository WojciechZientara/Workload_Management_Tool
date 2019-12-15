<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="/WEB-INF/assets/header.jsp"%>
<div id="main" class="mainFrame, landingFrame">
        <span class="landingPage">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Fusce in convallis ipsum, et luctus massa. Nam id venenatis mi, vel volutpat nibh.
            Integer hendrerit tortor molestie semper posuere. Suspendisse sed euismod eros.
            Nunc mollis leo eget neque ultricies imperdiet. Morbi nec ex vitae quam volutpat elementum quis et neque.
            Sed vel sem purus. Nulla tincidunt fermentum ante, ut consectetur sem pretium vitae.
            Sed fringilla orci vitae risus pretium vulputate. Nunc suscipit laoreet libero quis condimentum.
            Aliquam porttitor sed mi lobortis vulputate.
        </span>
    <span id="login" class="landingPage">
            Dane użytkownika:<br><br>
            <form method="post">
                <table>
                    <tr><td>User: </td><td><input type="text" name="username" /></td></tr>
                    <tr><td>Hasło: </td><td><input type="password" name="password" /></td></tr>
                    <tr><td></td><td>
                        <c:if test="${requestScope['javax.servlet.forward.query_string'] == 'error'}">
                            <span class="error">Błędny login lub hasło</span>
                        </c:if>
                    </td></tr>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <tr><td></td><td><input type="submit" value="Zaloguj się" class="submitBtn"></td></tr>
                </table>
                <br><br>Nie masz konta? Zgłoś się do admina.
            </form>
        </span>
</div>
<%@include file="/WEB-INF/assets/footer.jsp"%>