<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="*|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates select="node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="target[@name='init']">
		<tstamp>
			<format property="now" pattern="yyyyMMddHHmmss" />
		</tstamp>

		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates select="node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="project/@default">
		<xsl:attribute name="default">build.update.jar</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="target[@name='build.update.jar']/@depends">
		<xsl:attribute name="depends">clean</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="eclipse.versionReplacer/@version">
		<xsl:attribute name="version">1.0.0.${now}</xsl:attribute>
	</xsl:template>

	<xsl:template
		match="target[@name='build.update.jar']/jar/@destfile">
		<xsl:attribute name="destfile">${plugin.destination}/<xsl:value-of select="/project/@name"/>.jar</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>