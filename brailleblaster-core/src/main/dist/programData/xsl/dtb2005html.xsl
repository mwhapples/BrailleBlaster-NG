<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY ictlatts "@id | @class | @style | @title | @xml:lang">
        <!ENTITY itlatts "@id | @style | @title | @xml:lang">
        ]>

<xsl:stylesheet version="1.0" exclude-result-prefixes="dtb out"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:epub="http://www.idpf.org/2007/ops"
                xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:pls="http://www.w3.org/2005/01/pronunciation-lexicon"
                xmlns:ssml="http://www.w3.org/2001/10/synthesis"
                xmlns:svg="http://www.w3.org/2000/svg"
>
    <!--  American Printing House for the Blind, Inc.
      Copyright (c) 2004 - 2008, All Rights Reserved.

     Convert Daisy/NISO Dtbook 2005-2 xml to xhtml,
     using the mxxml DOM to transform the document to an
       xhtml document text. Most dtbook specific elements
       are converted to div with class attribute by element
       name.

       To use the output in a browser, several post transform
       steps are needed. Add the amp entity to any ampesand
       characters. Replace the xhtml root element with the formal
       '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">' + xhtmlNodes + '</html>'
       and format the document with line breaks before
       saving into a text file.

    The html root element was not used to  avoids the undesirable
    transform output of html formatted text that is not XML DOM
    compliant and it also avoids xmlns attributes that
    appear when the xhtml namwspace is used.

    5/7/2008 - added 2005-2 namespace to document declaration
    and support for xml namespace to xml output for xhtml

    specify processing parameters:
-->
    <xsl:strip-space elements="*"/>
    <xsl:preserve-space elements="code samp "/>

    <xsl:output method="html" encoding="utf-8" indent="yes" omit-xml-declaration="no"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- root dtbook to html -->
    <xsl:template match="dtb:dtbook">
        <xsl:element name="html" namespace="http://www.w3.org/1999/xhtml">
            <xsl:if test="@xml:lang">
                <xsl:copy-of select="@xml:lang"/>
            </xsl:if>
            <xsl:if test="@dir">
                <xsl:copy-of select="@dir"/>
            </xsl:if>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="dtb:head">
        <head>
            <!--<title>
                <xsl:value-of select="dtb:title/text()" />
              </title>
           <xsl:if test="not(link/@rel)">
               <link rel="StyleSheet" href="aph_base.css" type="text/css" media="screen,print" />
           </xsl:if>-->
            <xsl:apply-templates select="dtb:meta | dtb:link | dtb:style"/>
        </head>
    </xsl:template>

    <xsl:template match="dtb:meta">
        <meta>
            <xsl:copy-of select="@*"/>
        </meta>
    </xsl:template>

    <xsl:template match="dtb:link">
        <link>
            <xsl:copy-of select="rel | href | type | media"/>
        </link>
    </xsl:template>

    <xsl:template match="dtb:style">
        <style>
            <xsl:copy-of select="text()"/>
        </style>
    </xsl:template>

    <!-- structure elements -->
    <xsl:template match="dtb:book">
        <body>
            <xsl:apply-templates/>
        </body>
    </xsl:template>

    <xsl:template match="dtb:frontmatter">
        <div class="frontmatter">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:bodymatter">
        <div class="bodymatter">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:rearmatter">
        <div class="rearmatter">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:level1">
        <section class="level1">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <xsl:template match="dtb:level2">
        <div class="level2">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:level3">
        <div class="level3">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:level4">
        <div class="level4">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:level5">
        <div class="level5">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:level6">
        <div class="level6">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:level">
        <div class="level">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- block elements -->
    <xsl:template match="dtb:covertitle">
        <p class="covertitle">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <xsl:template match="dtb:p">
        <p>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <xsl:template match="dtb:pagenum">
        <span class="pagenum">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:list/dtb:hd">
        <li class="hd">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </li>
    </xsl:template>

    <xsl:template match="dtb:list/dtb:pagenum" priority="1">
        <li>
            <span class="pagenum">
                <xsl:copy-of select="&itlatts;"/>
                <xsl:apply-templates/>
            </span>
        </li>
    </xsl:template>

    <xsl:template match="dtb:blockquote/dtb:pagenum">
        <span class="pagenum">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:address">
        <address>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </address>
    </xsl:template>

    <xsl:template match="dtb:h1">
        <h1>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </h1>
    </xsl:template>

    <xsl:template match="dtb:h2">
        <h2>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </h2>
    </xsl:template>

    <xsl:template match="dtb:h3">
        <h3>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </h3>
    </xsl:template>

    <xsl:template match="dtb:h4">
        <h4>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </h4>
    </xsl:template>

    <xsl:template match="dtb:h5">
        <h5>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </h5>
    </xsl:template>

    <xsl:template match="dtb:h6">
        <h6>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </h6>
    </xsl:template>

    <xsl:template match="dtb:bridgehead">
        <div class="bridgehead">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:list[not(@type)]">
        <ul>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>

    <xsl:template match="dtb:list[@type='ol']">
        <xsl:choose>
            <xsl:when test="@enum='A'">
                <ol class="upperalpha">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:copy-of select="@start"/>
                    <xsl:apply-templates/>
                </ol>
            </xsl:when>
            <xsl:when test="@enum='a'">
                <ol class="loweralpha">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:copy-of select="@start"/>
                    <xsl:apply-templates/>
                </ol>
            </xsl:when>
            <xsl:when test="@enum='I'">
                <ol class="upperroman">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:copy-of select="@start"/>
                    <xsl:apply-templates/>
                </ol>
            </xsl:when>
            <xsl:when test="@enum='i'">
                <ol class="lowerroman">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:copy-of select="@start"/>
                    <xsl:apply-templates/>
                </ol>
            </xsl:when>
            <xsl:otherwise>
                <ol class="decimal">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:copy-of select="@start"/>
                    <xsl:apply-templates/>
                </ol>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="dtb:list[@type='ul']">
        <xsl:choose>
            <xsl:when test="@enum='circle'">
                <ul class="circle">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:apply-templates/>
                </ul>
            </xsl:when>
            <xsl:when test="@enum='disc'">
                <ul class="disc">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:apply-templates/>
                </ul>
            </xsl:when>
            <xsl:when test="../@enum='square'">
                <ul class="square">
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:apply-templates/>
                </ul>
            </xsl:when>
            <xsl:otherwise>
                <ul>
                    <xsl:copy-of select="&itlatts;"/>
                    <xsl:apply-templates/>
                </ul>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="dtb:list[@type='pl']">
        <ul class="plain">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>

    <xsl:template match="dtb:li">
        <li>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </li>
    </xsl:template>

    <xsl:template match="dtb:lic">
        <span class="lic">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:blockquote">
        <blockquote>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </blockquote>
    </xsl:template>

    <xsl:template match="dtb:br">
        <br/>
        <xsl:copy-of select="&itlatts;"/>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="dtb:noteref">
        <span class="noteref">
            <xsl:copy-of select="&itlatts;"/>
            <a href="{@idref}">
                <xsl:apply-templates/>
            </a>
        </span>
    </xsl:template>

    <!-- img elements -->
    <xsl:template match="dtb:img">
        <img>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:copy-of select="@src | @alt | @longdesc | @height | @width"/>
            <xsl:apply-templates/>
        </img>
    </xsl:template>

    <xsl:template match="dtb:caption">
        <caption>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </caption>
    </xsl:template>

    <xsl:template match="dtb:imggroup/dtb:caption">
        <p class="caption">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <!--<xsl:template match="dtb:imggroup">
      <figure class="imggroup"><xsl:copy-of select="&itlatts;"/><xsl:apply-templates/></figure>
    </xsl:template>-->

    <xsl:template match="dtb:div">
        <div>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- dtb elements -->
    <xsl:template match="dtb:annotation">
        <div class="annotation">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:byline">
        <div class="byline">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:dateline">
        <div class="dateline">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:epigraph">
        <div class="epigraph">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:note">
        <div class="note">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:sidebar">
        <div class="sidebar">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:hd">
        <div class="hd">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- data list elements -->
    <xsl:template match="dtb:dl">
        <dl>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </dl>
    </xsl:template>

    <xsl:template match="dtb:dt">
        <dt>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </dt>
    </xsl:template>

    <xsl:template match="dtb:dd">
        <dd>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </dd>
    </xsl:template>

    <!-- table elements -->
    <xsl:template match="dtb:table">
        <table>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <xsl:template match="dtb:tbody">
        <tbody>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </tbody>
    </xsl:template>

    <xsl:template match="dtb:thead">
        <thead>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </thead>
    </xsl:template>

    <xsl:template match="dtb:tfoot">
        <tfoot>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </tfoot>
    </xsl:template>

    <xsl:template match="dtb:tr">
        <tr>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:copy-of select="@rowspan | @colspan"/>
            <xsl:apply-templates/>
        </tr>
    </xsl:template>

    <xsl:template match="dtb:th">
        <th>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:copy-of select="@rowspan | @colspan"/>
            <xsl:apply-templates/>
        </th>
    </xsl:template>

    <xsl:template match="dtb:td">
        <td>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:copy-of select="@rowspan|@colspan"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>

    <xsl:template match="dtb:colgroup">
        <colgroup>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </colgroup>
    </xsl:template>

    <xsl:template match="dtb:col">
        <col>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </col>
    </xsl:template>

    <!-- poem and citation -->
    <xsl:template match="dtb:poem">
        <div class="poem">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:poem/dtb:title">
        <p class="title">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <xsl:template match="dtb:cite/dtb:title">
        <span class="title">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:cite">
        <cite>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </cite>
    </xsl:template>

    <xsl:template match="dtb:code">
        <code>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </code>
    </xsl:template>

    <xsl:template match="dtb:kbd">
        <kbd>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </kbd>
    </xsl:template>

    <xsl:template match="dtb:q">
        <q>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </q>
    </xsl:template>

    <xsl:template match="dtb:samp">
        <samp>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </samp>
    </xsl:template>

    <xsl:template match="dtb:linegroup">
        <div class="linegroup">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:line">
        <div class="line">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="dtb:linenum">
        <span class="linenum">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <!--<xsl:template match="dtb:prodnote">
       <aside class="prodnote"><xsl:copy-of select="&itlatts;"/><xsl:apply-templates/></aside>
     </xsl:template>-->

    <!-- inlines elements -->
    <xsl:template match="dtb:a">
        <span class="anchor">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:em">
        <em>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </em>
    </xsl:template>

    <xsl:template match="dtb:span">
        <span>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:strong">
        <strong>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </strong>
    </xsl:template>

    <xsl:template match="dtb:abbr">
        <abbr>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </abbr>
    </xsl:template>

    <xsl:template match="dtb:acronym">
        <acronym>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </acronym>
    </xsl:template>

    <xsl:template match="dtb:bdo">
        <bdo>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </bdo>
    </xsl:template>

    <xsl:template match="dtb:dfn">
        <span class="dfn">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:sent">
        <span class="sent">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:w">
        <span class="word">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:sup">
        <sup>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </sup>
    </xsl:template>


    <xsl:template match="dtb:sub">
        <sub>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </sub>
    </xsl:template>

    <xsl:template match="dtb:a[@href]">
        <a>
            <xsl:copy-of select="&itlatts;"/>
            <xsl:copy-of select="@href"/>
            <xsl:apply-templates/>
        </a>
    </xsl:template>

    <xsl:template match="dtb:annoref">
        <span class="annoref">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:doctitle">
        <span class="doctitle">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:docauthor">
        <span class="docauthor">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="dtb:author">
        <span class="author">
            <xsl:copy-of select="&itlatts;"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

</xsl:stylesheet>
 
