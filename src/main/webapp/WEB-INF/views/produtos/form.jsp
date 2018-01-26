<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Livros de Java, Android, iPhone, Ruby, PHP e muito mais - Casa do Código</title>
</head>
<body>
	<form:form action="/casadocodigo/produtos" method="post" commandName="produto">
	    <div>
	        <label>Título</label>	        
	        <input type="text" name="titulo" />
	        <form:errors path="produto.titulo" />	        
	    </div>
	    <p/>
	    <div>
	        <label>Descrição</label>
	        <textarea rows="10" cols="20" name="descricao"></textarea>
	        <form:errors path="produto.descricao" />
	    </div>
	    <p/>
	    <div>
	        <label>Páginas</label>
	        <input type="text" name="paginas" />
	        <form:errors path="produto.paginas" />
	    </div>
	    <p/>

		<c:forEach items="${tipos}" var="tipoPreco" varStatus="status">
			<div>
				<label>${tipoPreco}</label>
				<input type="text" name="precos[${status.index}].valor" />
				<input type="hidden" name="precos[${status.index}].tipo" />
			</div>
		</c:forEach>
		
	    <p/>
	    <button type="submit">Cadastrar</button>
	</form:form>
</body>
</html>