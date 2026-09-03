package com.services;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <code>RestService</code> contém as funcionalidades padrões para consumir uma
 * entidade do sistema que estenda {@link Serializable}.<br />
 * <code>RestService</code> foi projetado para ser estendido em um
 * {@link @Service} que possa fazer a gerência do consumo de uma entidade alvo.
 *
 * @param <T> tipo que será consumido
 * @author <a href="mailto:felipe.rios.silva@outlook.com">Felipe Rios</a>
 * @author <a href="mailto:railson170@hotmail.com">Railson Silva</a>
 */
@SuppressWarnings("unchecked")
abstract class RestService<T extends Serializable> {

	private static final String DATE_EN = "yyyy-MM-dd";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${app.restservice.base}")
	private String BASE;

	private final Class<T> entity;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Cria <code>RestService</code> pegando a própria classe (super) genérica.
	 */
	RestService() {
		this.entity = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	<O> O getForEntity(String url) {
		return getForEntity(url, null);
	}

	<O> O getForEntity(String url, Map<String, String> uriVariables) {
		return getForEntity(url, uriVariables, this.entity);
	}

	<O> O getForEntity(String url, Map<String, ?> uriVariables, Class type) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(type);
		return readValue(getRequest(url, uriVariables), javaType);
	}

	<O> O getForEntity(String url, Map<String, String> uriVariables, TypeReference type) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(type);
		return readValue(getRequest(url, uriVariables), javaType);
	}

	<C> C getForList(String url) {
		return getForList(url, null);
	}

	<C> C getForList(String url, Map<String, ?> uriVariables) {
		return getForList(url, uriVariables, this.entity);
	}

	<C> C getForList(String url, Map<String, ?> uriVariables, Class type) {
		CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
		return readValue(getRequest(url, uriVariables), collectionType);
	}

	<C> C getForList(String url, Map<String, String> uriVariables, TypeReference type) {
		return getForEntity(url, uriVariables, type);
	}

	<O, R> O postForEntity(String url, R body) {
		return postForEntity(url, body, this.entity);
	}

	<O, R> O postForEntity(String url, R body, Class type) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(type);
		return readValue(postRequest(url, body), javaType);
	}

	<O, R> O postForEntity(String url, R body, TypeReference type) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(type);
		return readValue(postRequest(url, body), javaType);
	}

	<C, R> C postForList(String url, R body) {
		return postForList(url, body, this.entity);
	}

	<C, R> C postForList(String url, R body, Class type) {
		CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
		return readValue(postRequest(url, body), collectionType);
	}

	<C, R> C postForList(String url, R body, TypeReference type) {
		return postForEntity(url, body, type);
	}

	/*<O, R> O putForEntity(String url, R body, Object... uriVariables) {
		HttpEntity<R> request = new HttpEntity<>(body);
		ResponseEntity<String>
				response = this.restTemplate.exchange(url, HttpMethod.PUT, request,
				String.class, uriVariables);
		JavaType javaType =
				this.objectMapper.getTypeFactory().constructType(this.entity);
		return
				readValue(response, javaType);
	}

	void delete(String url, Object... uriVariables) {
		try {
			this.restTemplate.delete(url, uriVariables);
		} catch (RestClientException e) {
			this.logger.info(e.getMessage());
		}
	}*/

	private ResponseEntity<String> getRequest(String url, Map<String, ?> uriVariables) {
		try {
			if (!Objects.isNull(uriVariables)) {
				url += "?" + Joiner.on("&").withKeyValueSeparator("=").join(verifyMap(uriVariables));
			}
			return this.restTemplate.getForEntity(BASE.concat(url), String.class);
		} catch (HttpClientErrorException e) {
			requestErrorHandle(e, url);
		}
		return null;
	}

	public Map<String, String> verifyMap(Map<String, ?> uriVariables) {
		Map<String, String> uri = new HashMap<>();
		uriVariables.forEach((k, v) -> {
			if (v instanceof Date) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_EN);
				uri.put(k, dateFormat.format(v));
			} else if (Objects.nonNull(v)) {
				uri.put(k, v.toString());
			}
		});
		return uri;
	}

	private <R> ResponseEntity<String> postRequest(String url, R body) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<R> request = new HttpEntity<>(body, headers);
			return this.restTemplate.postForEntity(BASE.concat(url), request, String.class);
		} catch (HttpClientErrorException e) {
			requestErrorHandle(e, url);
		}
		return null;
	}

	private void requestErrorHandle(HttpClientErrorException e, String url) {
		if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
			this.logger.error(messageSource.getMessage("exception.url.not.found", null, LocaleContextHolder.getLocale()), url);
			throw new ObjectNotFoundException(messageSource.getMessage("exception.object.not.found", null, LocaleContextHolder.getLocale()));
		} else {
			this.logger.error(messageSource.getMessage("exception.http.rest.unrecognized", null, LocaleContextHolder.getLocale()), e.getMessage());
		}
	}

	private <O> O readValue(ResponseEntity<String> response, JavaType type) {
		O result = null;
		if (Objects.isNull(response)) {
			return result;
		} else {
			if ((response.getStatusCode() == HttpStatus.OK) || (response.getStatusCode() == HttpStatus.CREATED)) {
				try {
					if (type.getRawClass().equals(String.class)) {
						result = (O) response.getBody();
					} else if (Objects.isNull(response.getBody())) {
						result = null;
					} else {
						result = this.objectMapper.readValue(response.getBody(), type);
					}
				} catch (JacksonException e) {
					this.logger.error(messageSource.getMessage("exception.json.parse", null, LocaleContextHolder.getLocale()), e.getMessage());
				}
			} else {
				this.logger.error(messageSource.getMessage("exception.http.rest.unrecognized", null, LocaleContextHolder.getLocale()),
						response.getStatusCode());
			}
			return result;
		}
	}
}
