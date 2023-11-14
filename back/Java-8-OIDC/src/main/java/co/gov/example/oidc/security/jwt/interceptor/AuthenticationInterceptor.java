package co.gov.example.oidc.security.jwt.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import co.gov.example.oidc.security.jwt.annotations.Authorize;

public class AuthenticationInterceptor implements HandlerInterceptor {

	@Value("${ids4.jwks.url}")
	private String jwksUrl;

	@Value("${ids4.issuer}")
	private String issuer;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		if (method.isAnnotationPresent(Authorize.class)) {
			String originToken = request.getHeader("Authorization");
			if (originToken == null || originToken.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}
			try {
				String token = getToken(originToken);
				System.out.println("Valor del token ******************** " + token);
				DecodedJWT jwt = JWT.decode(token);
				JwkProvider http = new UrlJwkProvider(new URL(jwksUrl));
				JwkProvider provider = new GuavaCachedJwkProvider(http);
				Jwk jwk = provider.get(jwt.getId());
				Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
				JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
				verifier.verify(token);
			} catch (JWTVerificationException exception) {
				PrintWriter writer = response.getWriter();
				writer.write(exception.getMessage());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				originToken = null;
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	private String getToken(String originToken) {
		String[] arr = originToken.split(" ");
		return arr[1];
	}
}