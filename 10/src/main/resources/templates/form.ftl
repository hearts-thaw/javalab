<#ftl encoding="utf-8">
<form action="${action}" method="${method}">
    <#list inps as inp>
        <input type="${inp.getType()}" name="${inp.getName()}" placeholder="${inp.getPlaceholder()}">
    </#list>
</form>
