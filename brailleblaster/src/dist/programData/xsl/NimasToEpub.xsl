<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:f="http://www.daisy.org/ns/pipeline/internal-functions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:dtbook="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:epub="http://www.idpf.org/2007/ops" xmlns="http://www.w3.org/1999/xhtml" xpath-default-namespace="http://www.w3.org/1999/xhtml" exclude-result-prefixes="#all"
    xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output indent="yes" exclude-result-prefixes="#all"/>

    <xsl:variable name="vocab-default"
        select="('acknowledgments','afterword','annoref','annotation','appendix','backmatter','biblioentry','bibliography','bodymatter','bridgehead','chapter','colophon','concluding-sentence','conclusion','contributors','copyright-page','cover','covertitle','dedication','division','epigraph','epilogue','errata','footnote','footnotes','foreword','frontmatter','fulltitle','glossary','glossdef','glossterm','halftitle','halftitlepage','help','imprimatur','imprint','index','introduction','keyword','landmarks','list','list-item','loi','lot','marginalia','note','noteref','notice','other-credits','pagebreak','page-list','part','practice','preamble','preface','prologue','rearnote','rearnotes','sidebar','subchapter','subtitle','table','table-cell','table-row','title','titlepage','toc','topic-sentence','volume','warning')"/>
    <xsl:variable name="vocab-z3998"
        select="('abbreviations','acknowledgments','acronym','actor','afterword','alteration','annoref','annotation','appendix','article','aside','attribution','author','award','backmatter','bcc','bibliography','biographical-note','bodymatter','cardinal','catalogue','cc','chapter','citation','clarification','collection','colophon','commentary','commentator','compound','concluding-sentence','conclusion','continuation','continuation-of','contributors','coordinate','correction','covertitle','currency','decimal','decorative','dedication','diary','diary-entry','discography','division','drama','dramatis-personae','editor','editorial-note','email','email-message','epigraph','epilogue','errata','essay','event','example','family-name','fiction','figure','filmography','footnote','footnotes','foreword','fraction','from','frontispiece','frontmatter','ftp','fulltitle','gallery','general-editor','geographic','given-name','glossary','grant-acknowledgment','grapheme','halftitle','halftitle-page','help','homograph','http','hymn','illustration','image-placeholder','imprimatur','imprint','index','initialism','introduction','introductory-note','ip','isbn','keyword','letter','loi','lot','lyrics','marginalia','measure','mixed','morpheme','name-title','nationality','non-fiction','nonresolving-citation','nonresolving-reference','note','noteref','notice','orderedlist','ordinal','organization','other-credits','pagebreak','page-footer','page-header','part','percentage','persona','personal-name','pgroup','phone','phoneme','photograph','phrase','place','plate','poem','portmanteau','postal','postal-code','postscript','practice','preamble','preface','prefix','presentation','primary','product','production','prologue','promotional-copy','published-works','publisher-address','publisher-logo','range','ratio','rearnote','rearnotes','recipient','recto','reference','republisher','resolving-reference','result','role-description','roman','root','salutation','scene','secondary','section','sender','sentence','sidebar','signature','song','speech','stage-direction','stem','structure','subchapter','subject','subsection','subtitle','suffix','surname','taxonomy','tertiary','text','textbook','t-form','timeline','title','title-page','to','toc','topic-sentence','translator','translator-note','truncation','unorderedlist','valediction','verse','verso','v-form','volume','warning','weight','word')"/>

    <xsl:template match="text()|comment()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="*">
        <xsl:comment select="concat('No template for element: ',name())"/>
    </xsl:template>

    <xsl:template name="coreattrs">
        <xsl:param name="classes" select="()" tunnel="yes"/>
        <xsl:param name="except" select="()" tunnel="yes"/>
        <xsl:param name="all-ids" select="()" tunnel="yes"/>
        <xsl:variable name="is-first-level" select="boolean((self::dtbook:level or self::dtbook:level1) and (parent::dtbook:frontmatter or parent::dtbook:bodymatter or parent::dtbook:rearmatter))"/>
        <xsl:if test="$is-first-level">
            <!--
                the frontmatter/bodymatter/rearmatter does not have corresponding elements in HTML and is removed;
                try preserving the attributes on the closest sectioning element(s) when possible
            -->
            <xsl:copy-of select="parent::*/(@title|@xml:space)[not(name()=$except)]"/>
            <xsl:if test="not(preceding-sibling::dtbook:level or preceding-sibling::dtbook:level1)">
                <xsl:copy-of select="parent::*[not(name()=$except)]/@id"/>
            </xsl:if>
        </xsl:if>
        <xsl:copy-of select="(@id|@title|@xml:space)[not(name()=$except)]"/>
        <xsl:if
            test="not(@id) and not(local-name()=('book','span','p','div','tr','th','td','link','br','line','linenum','title','author','em','strong','dfn','kbd','code','samp','cite','abbr','acronym','sub','sup','bdo','sent','w','pagenum','docauthor','bridgehead','dd','lic','thead','tfoot','tbody','colgroup','col') and namespace-uri()='http://www.daisy.org/z3986/2005/dtbook/')">
            <xsl:attribute name="id" select="f:generate-pretty-id(.,$all-ids)"/>
        </xsl:if>
        <xsl:call-template name="classes-and-types">
            <xsl:with-param name="classes" select="(if ($is-first-level) then tokenize(parent::*/@class,'\s+') else (), $classes)" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="i18n">
        <xsl:param name="except" select="()" tunnel="yes"/>
        <xsl:variable name="is-first-level" select="boolean((self::dtbook:level or self::dtbook:level1) and (parent::dtbook:frontmatter or parent::dtbook:bodymatter or parent::dtbook:rearmatter))"/>
        <xsl:if test="$is-first-level">
            <!--
                the frontmatter/bodymatter/rearmatter does not have corresponding elements in HTML and is removed;
                try preserving the attributes on the closest sectioning element(s) when possible
            -->
            <xsl:copy-of select="parent::*/(@xml:lang|@dir)[not(name()=$except)]"/>
        </xsl:if>
        <xsl:copy-of select="(@xml:lang|@dir)[not(name()=$except)]"/>
    </xsl:template>

    <xsl:template name="classes-and-types">
        <xsl:param name="classes" select="()" tunnel="yes"/>
        <xsl:param name="types" select="()" tunnel="yes"/>
        <xsl:param name="except" select="()" tunnel="yes"/>
        <xsl:param name="except-classes" select="()" tunnel="yes"/>
        <xsl:param name="except-types" select="()" tunnel="yes"/>
        <xsl:variable name="showin" select="for $s in (@showin) return concat('showin-',$s)"/>
        <xsl:variable name="old-classes" select="tokenize(@class,'\s+')"/>

        <!-- non-standard classes are assumed to be already fixed before running this XSLT (for instance, 'jacketcopy' => 'cover' and 'endnote' => 'rearnote') -->
        <xsl:variable name="epub-types">
            <xsl:for-each select="$old-classes">
                <xsl:sequence select="if (.=$vocab-default) then . else if (.=$vocab-z3998) then concat('z3998:',.) else ()"/>
            </xsl:for-each>
            <xsl:value-of select="''"/>
        </xsl:variable>
        <xsl:variable name="epub-types" select="($types, $epub-types)[not(.='') and not(.=$except-types)]"/>
        <xsl:variable name="epub-types"
            select="($epub-types, if ($epub-types='bodymatter' and not($epub-types=('prologue','preface','part','chapter','conclusion','epilogue'))) then 'chapter' else ())"/>
        <xsl:variable name="epub-types"
            select="($epub-types, if ((self::dtbook:level2 or self::dtbook:level[count(ancestor::level)=1]) and (ancestor::dtbook:level1|ancestor::dtbook:level[not(ancestor::dtbook:level)])/tokenize(@class,'\s+')='part' and not($epub-types=('prologue','preface','chapter','conclusion','epilogue'))) then 'chapter' else ())"/>

        <xsl:variable name="classes" select="($classes, $old-classes[not(.=($vocab-default,$vocab-z3998))], $showin)[not(.='') and not(.=$except-classes)]"/>

        <xsl:if test="count($classes) and not('_class'=$except)">
            <xsl:attribute name="class" select="string-join(distinct-values($classes),' ')"/>
        </xsl:if>
        <xsl:if test="count($epub-types) and not('_epub:type'=$except)">
            <xsl:attribute name="epub:type" select="string-join(distinct-values($epub-types),' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="attrs">
        <xsl:call-template name="coreattrs"/>
        <xsl:call-template name="i18n"/>
        <!-- ignore @smilref -->
        <!-- @showin handled by coreattrs -->
    </xsl:template>

    <xsl:template name="attrsrqd">
        <xsl:call-template name="coreattrs"/>
        <xsl:call-template name="i18n"/>
        <!-- ignore @smilref -->
        <!-- @showin handled by classes-and-types -->
    </xsl:template>

    <xsl:template match="dtbook:dtbook">
        <xsl:variable name="all-ids" select=".//@id"/>
        <html>
            <xsl:namespace name="nordic" select="'http://www.mtm.se/epub/'"/>
            <xsl:attribute name="epub:prefix" select="'z3998: http://www.daisy.org/z3998/2012/vocab/structure/#'"/>
            <xsl:call-template name="attlist.dtbook">
                <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
            </xsl:call-template>
            <xsl:apply-templates select="node()">
                <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
            </xsl:apply-templates>
        </html>
    </xsl:template>

    <xsl:template name="attlist.dtbook">
        <!-- ignore @version -->
        <xsl:call-template name="i18n"/>
    </xsl:template>

    <xsl:template name="headmisc">
        <xsl:apply-templates select="node()"/>
    </xsl:template>

    <xsl:template match="dtbook:head">
        <head>
            <xsl:call-template name="attlist.head"/>
            <meta charset="UTF-8"/>
            <title>
                <xsl:value-of select="string((dtbook:meta[matches(@name,'dc:title','i')])[1]/@content)"/>
            </title>
            <meta name="viewport" content="width=device-width"/>
            <meta name="dc:identifier" content="{string((dtbook:meta[matches(@name,'dtb:uid')])[1]/@content)}"/>
            <meta name="nordic:guidelines" content="2015-1"/>
            <xsl:for-each select="dtbook:meta[starts-with(@name,'track:') and not(@name='track:Guidelines')]">
                <meta name="nordic:{lower-case(substring-after(@name,'track:'))}" content="{@content}"/>
            </xsl:for-each>
            <xsl:call-template name="headmisc"/>
            <style type="text/css" xml:space="preserve"><![CDATA[
                .initialism{
                    -epub-speak-as:spell-out;
                }
                .list-preformatted{
                    list-style-type:none;
                }
                table[class ^= "table-rules-"],
                table[class *= " table-rules-"]{
                    border-width:thin;
                    border-style:hidden;
                }
                table[class ^= "table-rules-"]:not(.table-rules-none),
                table[class *= " table-rules-"]:not(.table-rules-none){
                    border-collapse:collapse;
                }
                table[class ^= "table-rules-"] td,
                table[class *= " table-rules-"] td{
                    border-width:thin;
                    border-style:none;
                }
                table[class ^= "table-rules-"] th,
                table[class *= " table-rules-"] th{
                    border-width:thin;
                    border-style:none;
                }
                table.table-rules-none td,
                table.table-rules-none th{
                    border-width:thin;
                    border-style:hidden;
                }
                table.table-rules-all td,
                table.table-rules-all th{
                    border-width:thin;
                    border-style:solid;
                }
                table.table-rules-cols td,
                table.table-rules-cols th{
                    border-left-width:thin;
                    border-right-width:thin;
                    border-left-style:solid;
                    border-right-style:solid;
                }
                table.table-rules-rows tr{
                    border-top-width:thin;
                    border-bottom-width:thin;
                    border-top-style:solid;
                    border-bottom-style:solid;
                }
                table.table-rules-groups colgroup{
                    border-left-width:thin;
                    border-right-width:thin;
                    border-left-style:solid;
                    border-right-style:solid;
                }
                table.table-rules-groups tfoot,
                table.table-rules-groups thead,
                table.table-rules-groups tbody{
                    border-top-width:thin;
                    border-bottom-width:thin;
                    border-top-style:solid;
                    border-bottom-style:solid;
                }
                table[class ^= "table-frame-"],
                table[class *= " table-frame-"]{
                    border:thin hidden;
                }
                table.table-frame-void{
                    border-style:hidden;
                }
                table.table-frame-above{
                    border-style:outset hidden hidden hidden;
                }
                table.table-frame-below{
                    border-style:hidden hidden outset hidden;
                }
                table.table-frame-lhs{
                    border-style:hidden hidden hidden outset;
                }
                table.table-frame-rhs{
                    border-style:hidden outset hidden hidden;
                }
                table.table-frame-hsides{
                    border-style:outset hidden;
                }
                table.table-frame-vsides{
                    border-style:hidden outset;
                }
                table.table-frame-box{
                    border-style:outset;
                }
                table.table-frame-border{
                    border-style:outset;
                }]]></style>
            <xsl:if test="@profile">
                <link rel="profile" href="{@profile}"/>
            </xsl:if>
        </head>
    </xsl:template>

    <xsl:template name="attlist.head">
        <xsl:call-template name="i18n"/>
        <!-- @profile handled by main head element test -->
    </xsl:template>

    <xsl:template match="dtbook:link">
        <link>
            <xsl:call-template name="attlist.link"/>
        </link>
    </xsl:template>

    <xsl:template name="attlist.link">
        <xsl:call-template name="attrs"/>
        <xsl:copy-of select="@href|@hreflang|@type|@rel|@media"/>
        <!-- @charset and @rev are dropped -->
    </xsl:template>

    <xsl:template match="dtbook:meta">
        <xsl:choose>
            <xsl:when test="matches(@name,'dc:title','i') or matches(@name,'dc:identifier','i') or matches(@name,'dc:format','i') or starts-with(@name,'track:')"/>
            <xsl:otherwise>
                <meta>
                    <xsl:call-template name="attlist.meta"/>
                </meta>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="attlist.meta">
        <xsl:call-template name="i18n"/>
        <xsl:copy-of select="@content|@http-equiv"/>
        <xsl:choose>
            <xsl:when test="matches(@name,'dc:.*','i')">
                <xsl:attribute name="name" select="lower-case(@name)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="name" select="@name"/>
            </xsl:otherwise>
        </xsl:choose>
        <!-- @scheme is dropped -->
    </xsl:template>

    <xsl:template match="dtbook:book">
        <body>
            <xsl:call-template name="attlist.book"/>
            <xsl:apply-templates select="node()"/>
        </body>
    </xsl:template>

    <xsl:template name="attlist.book">
        <xsl:call-template name="attrs">
            <xsl:with-param name="except" select="'id'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="cover">
        <section>
            <xsl:call-template name="attrs">
                <xsl:with-param name="types" select="'cover'" tunnel="yes"/>
            </xsl:call-template>
            <xsl:apply-templates select="node()"/>
        </section>
    </xsl:template>

    <xsl:template name="titlepage">
        <section>
            <xsl:call-template name="attlist.frontmatter">
                <xsl:with-param name="types" select="'titlepage'" tunnel="yes"/>
            </xsl:call-template>
            <xsl:apply-templates select="node()"/>
        </section>
    </xsl:template>

    <xsl:template match="dtbook:frontmatter">

        <xsl:if test="dtbook:doctitle | dtbook:covertitle | dtbook:docauthor">
            <header>
                <xsl:apply-templates select="dtbook:doctitle | dtbook:covertitle | dtbook:docauthor"/>
            </header>
        </xsl:if>

        <xsl:for-each select="dtbook:level1 | dtbook:level">
            <xsl:choose>

                <!-- cover -->
                <xsl:when test="f:classes(.)=('cover','jacketcopy')">
                    <xsl:apply-templates
                        select="if (not(preceding-sibling::*)) then preceding-sibling::comment() else (preceding-sibling::comment() intersect preceding-sibling::*[1]/following-sibling::comment())"/>

                    <xsl:call-template name="cover"/>
                </xsl:when>

                <!-- title page -->
                <xsl:when test="f:classes(.)='titlepage'">
                    <xsl:apply-templates
                        select="if (not(preceding-sibling::*)) then preceding-sibling::comment() else (preceding-sibling::comment() intersect preceding-sibling::*[1]/following-sibling::comment())"/>

                    <xsl:call-template name="titlepage"/>
                </xsl:when>

                <!-- the rest of the frontmatter -->
                <xsl:otherwise>
                    <xsl:apply-templates
                        select="if (not(preceding-sibling::*)) then preceding-sibling::comment() else (preceding-sibling::comment() intersect preceding-sibling::*[1]/following-sibling::comment())"/>
                    <xsl:apply-templates select="."/>
                </xsl:otherwise>

            </xsl:choose>
        </xsl:for-each>

        <xsl:apply-templates select="*[last()]/following-sibling::comment()"/>

    </xsl:template>

    <xsl:template name="attlist.frontmatter">
        <xsl:param name="types" tunnel="yes"/>
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="('frontmatter',$types)" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:bodymatter">
        <!-- all attributes on bodymatter will be lost -->
        <xsl:apply-templates select="node()"/>
    </xsl:template>

    <xsl:template name="attlist.bodymatter">
        <xsl:param name="types" tunnel="yes"/>
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="('bodymatter',$types)" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:rearmatter">
        <!-- all attributes on rearmatter will be lost -->
        <xsl:apply-templates select="node()"/>
    </xsl:template>

    <xsl:template name="attlist.rearmatter">
        <xsl:param name="types" tunnel="yes"/>
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="('backmatter',$types)" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:level | dtbook:level1 | dtbook:level2 | dtbook:level3 | dtbook:level4 | dtbook:level5 | dtbook:level6">
        <xsl:element name="{if (f:classes(.)='article') then 'article' else 'section'}">
            <xsl:call-template name="attlist.level"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.level">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types"
                select="if (ancestor::*[self::dtbook:level or self::dtbook:level1 or self::dtbook:level2 or self::dtbook:level3 or self::dtbook:level4 or self::dtbook:level5 or self::dtbook:level6]) then () else if (ancestor::dtbook:frontmatter) then 'frontmatter' else if (ancestor::dtbook:bodymatter) then 'bodymatter' else 'backmatter'"
                tunnel="yes"/>
        </xsl:call-template>
        <!-- @depth is removed, it is implicit anyway -->
    </xsl:template>

    <xsl:template match="dtbook:br">
        <br>
            <xsl:call-template name="attlist.br"/>
        </br>
    </xsl:template>

    <xsl:template name="attlist.br">
        <xsl:call-template name="coreattrs"/>
    </xsl:template>

    <xsl:template match="dtbook:line">
        <p>
            <xsl:call-template name="attlist.line"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <xsl:template name="attlist.line">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'line'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:linenum">
        <span>
            <xsl:call-template name="attlist.linenum"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.linenum">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'linenum'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:address">
        <address>
            <xsl:call-template name="attlist.address"/>
            <xsl:apply-templates select="node()"/>
        </address>
    </xsl:template>

    <xsl:template name="attlist.address">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:div">
        <div>
            <xsl:call-template name="attlist.div"/>
            <xsl:apply-templates select="node()"/>
        </div>
    </xsl:template>

    <xsl:template name="attlist.div">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:title">
        <strong>
            <xsl:call-template name="attlist.title"/>
            <xsl:apply-templates select="node()"/>
        </strong>
    </xsl:template>

    <xsl:template name="attlist.title">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'title'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:author">
        <span>
            <xsl:call-template name="attlist.author"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.author">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:author'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:prodnote">
        <xsl:choose>
            <xsl:when test="parent::*[tokenize(@class,'\s+')='cover']">
                <section>
                    <xsl:call-template name="attlist.prodnote"/>
                    <xsl:apply-templates select="node()"/>
                </section>
            </xsl:when>
            <xsl:otherwise>
                <aside>
                    <xsl:call-template name="attlist.prodnote"/>
                    <xsl:apply-templates select="node()"/>
                </aside>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="attlist.prodnote">
        <xsl:param name="all-ids" select="()" tunnel="yes"/>
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:production'" tunnel="yes"/>
            <xsl:with-param name="classes" select="if (@render) then concat('render-',@render) else ()" tunnel="yes"/>
            <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
        </xsl:call-template>
        <!-- @imgref is dropped, the relationship is preserved in the corresponding img/@longdesc -->
        <xsl:if test="not(@id)">
            <xsl:attribute name="id" select="f:generate-pretty-id(.,$all-ids)"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:sidebar">
        <xsl:choose>
            <xsl:when test="@render='required'">
                <figure>
                    <xsl:call-template name="attlist.sidebar"/>
                    <xsl:apply-templates select="node()"/>
                </figure>
            </xsl:when>
            <xsl:otherwise>
                <aside>
                    <xsl:call-template name="attlist.sidebar"/>
                    <xsl:apply-templates select="node()"/>
                </aside>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="attlist.sidebar">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'sidebar'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:note">
        <aside>
            <xsl:call-template name="attlist.note"/>
            <xsl:apply-templates select="node()"/>
        </aside>
    </xsl:template>

    <xsl:template name="attlist.note">
        <xsl:call-template name="attrsrqd">
            <xsl:with-param name="types" select="'note'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:annotation">
        <aside>
            <xsl:call-template name="attlist.annotation"/>
            <xsl:apply-templates select="node()"/>
        </aside>
    </xsl:template>

    <xsl:template name="attlist.annotation">
        <xsl:call-template name="attrsrqd">
            <xsl:with-param name="types" select="'annotation'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:epigraph">
        <p>
            <xsl:call-template name="attlist.epigraph"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <xsl:template name="attlist.epigraph">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'epigraph'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:byline">
        <span>
            <xsl:call-template name="attlist.byline"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.byline">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'byline'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:dateline">
        <span>
            <xsl:call-template name="attlist.dateline"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.dateline">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'dateline'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:linegroup">
        <xsl:element name="{if (dtbook:hd) then 'section' else 'div'}">
            <xsl:call-template name="attlist.linegroup"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.linegroup">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="if (parent::dtbook:poem) then 'z3998:verse' else ()" tunnel="yes"/>
            <xsl:with-param name="classes" select="'linegroup'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:poem">
        <xsl:element name="{if (dtbook:hd) then 'section' else 'div'}">
            <xsl:call-template name="attlist.poem"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.poem">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:poem'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:a">
        <a>
            <xsl:call-template name="attlist.a"/>
            <xsl:apply-templates select="node()"/>
        </a>
    </xsl:template>

    <xsl:template name="attlist.a">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="(if (@external) then concat('external-',@external) else (), if (@rev) then concat('rev-',@rev) else ())" tunnel="yes"/>
            <xsl:with-param name="exclude-classes" select="for $target in (f:classes(.)[matches(.,'^target-')]) return $target" tunnel="yes"/>
        </xsl:call-template>
        <xsl:copy-of select="@type|@href|@hreflang|@rel|@accesskey|@tabindex"/>
        <!-- @rev is dropped since it's not supported in HTML5 -->

        <xsl:choose>
            <xsl:when test="f:classes(.)[matches(.,'^target--')]">
                <xsl:attribute name="target" select="replace((f:classes(.)[matches(.,'^target--')])[1],'^target--','_')"/>
            </xsl:when>
            <xsl:when test="f:classes(.)[matches(.,'^target-')]">
                <xsl:attribute name="target" select="replace((f:classes(.)[matches(.,'^target-')])[1],'^target-','')"/>
            </xsl:when>
            <xsl:when test="@external='true'">
                <xsl:attribute name="target" select="'_blank'"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="dtbook:em">
        <em>
            <xsl:call-template name="attlist.em"/>
            <xsl:apply-templates select="node()"/>
        </em>
    </xsl:template>

    <xsl:template name="attlist.em">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:strong">
        <strong>
            <xsl:call-template name="attlist.strong"/>
            <xsl:apply-templates select="node()"/>
        </strong>
    </xsl:template>

    <xsl:template name="attlist.strong">
        <xsl:call-template name="attrs"/>
    </xsl:template>
    
    <!-- TODO: allow dtbook:span[f:classes(.)='definition'] -->
    <xsl:template match="dtbook:dfn">
        <dfn>
            <xsl:call-template name="attlist.dfn"/>
            <xsl:apply-templates select="node()"/>
        </dfn>
    </xsl:template>

    <xsl:template name="attlist.dfn">
        <xsl:call-template name="attrs"/>
    </xsl:template>
    
    <!-- TODO: allow dtbook:span[f:classes(.)='keyboard'] -->
    <xsl:template match="dtbook:kbd">
        <kbd>
            <xsl:call-template name="attlist.kbd"/>
            <xsl:apply-templates select="node()"/>
        </kbd>
    </xsl:template>

    <xsl:template name="attlist.kbd">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:code">
        <code>
            <xsl:call-template name="attlist.code"/>
            <xsl:apply-templates select="node()"/>
        </code>
    </xsl:template>

    <xsl:template name="attlist.code">
        <xsl:call-template name="attrs"/>
        <xsl:call-template name="i18n"/>
        <!-- ignore @smilref -->
        <!-- @showin handled by "attrs" -->
    </xsl:template>
    
    <!-- TODO: allow dtbook:span[f:classes(.)='example'] -->
    <xsl:template match="dtbook:samp">
        <samp>
            <xsl:call-template name="attlist.samp"/>
            <xsl:apply-templates select="node()"/>
        </samp>
    </xsl:template>

    <xsl:template name="attlist.samp">
        <xsl:call-template name="attrs"/>
        <xsl:call-template name="i18n"/>
        <!-- ignore @smilref -->
        <!-- @showin handled by "attrs" -->
    </xsl:template>
    
    <!-- TODO: allow dtbook:span[f:classes(.)='cite'] -->
    <xsl:template match="dtbook:cite">
        <cite>
            <xsl:call-template name="attlist.cite"/>
            <xsl:apply-templates select="node()"/>
        </cite>
    </xsl:template>

    <xsl:template name="attlist.cite">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:abbr | dtbook:span[@class='truncation']">
        <abbr>
            <xsl:call-template name="attlist.abbr"/>
            <xsl:apply-templates select="node()"/>
        </abbr>
    </xsl:template>

    <xsl:template name="attlist.abbr">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:acronym">
        <abbr>
            <xsl:call-template name="attlist.acronym"/>
            <xsl:apply-templates select="node()"/>
        </abbr>
    </xsl:template>

    <xsl:template name="attlist.acronym">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="if (@pronounce='no') then 'z3998:initialism' else 'z3998:acronym'" tunnel="yes"/>
            <xsl:with-param name="classes" select="if (@pronounce='no') then 'initialism' else ()" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:sub">
        <sub>
            <xsl:call-template name="attlist.sub"/>
            <xsl:apply-templates select="node()"/>
        </sub>
    </xsl:template>

    <xsl:template name="attlist.sub">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:sup">
        <sup>
            <xsl:call-template name="attlist.sup"/>
            <xsl:apply-templates select="node()"/>
        </sup>
    </xsl:template>

    <xsl:template name="attlist.sup">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:span">
        <span>
            <xsl:call-template name="attlist.span"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.span">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:bdo">
        <bdo>
            <xsl:call-template name="attlist.bdo"/>
            <xsl:apply-templates select="node()"/>
        </bdo>
    </xsl:template>

    <xsl:template name="attlist.bdo">
        <xsl:call-template name="coreattrs"/>
        <xsl:call-template name="i18n"/>
        <!-- ignore @smilref -->
        <!-- @showin handled by "coreattrs" -->
    </xsl:template>

    <xsl:template match="dtbook:sent">
        <span>
            <xsl:call-template name="attlist.sent"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.sent">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:sentence'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:w">
        <span>
            <xsl:call-template name="attlist.w"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.w">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:word'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:pagenum">
        <xsl:param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        <xsl:variable name="pagenum.parent" select="if (count($pagenum.parent/descendant-or-self::* intersect parent::*/ancestor-or-self::*) &gt;= 3) then parent::* else $pagenum.parent"/>
        <xsl:element name="{if (f:is-inline($pagenum.parent)) then 'span' else 'div'}">
            <xsl:call-template name="attlist.pagenum"/>
            <xsl:attribute name="title" select="normalize-space(.)"/>
            <!--
                NOTE: the title attribute is overwritten with the contents of the pagenum,
                so any pre-existing @title content is lost.
            -->
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.pagenum">
        <xsl:call-template name="attrsrqd">
            <xsl:with-param name="types" select="'pagebreak'" tunnel="yes"/>
            <xsl:with-param name="classes" select="concat('page-',(@page,'normal')[1])" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:noteref">
        <a>
            <xsl:call-template name="attlist.noteref"/>
            <xsl:apply-templates select="node()"/>
        </a>
    </xsl:template>

    <xsl:template name="attlist.noteref">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'noteref'" tunnel="yes"/>
        </xsl:call-template>
        <xsl:attribute name="href" select="@idref"/>
        <xsl:copy-of select="@type"/>
    </xsl:template>

    <xsl:template match="dtbook:annoref">
        <a>
            <xsl:call-template name="attlist.annoref"/>
            <xsl:apply-templates select="node()"/>
        </a>
    </xsl:template>

    <xsl:template name="attlist.annoref">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'annoref'" tunnel="yes"/>
        </xsl:call-template>
        <xsl:attribute name="href" select="@idref"/>
        <xsl:copy-of select="@type"/>
    </xsl:template>
    
    <!-- TODO: allow dtbook:span[f:classes(.)='quote'] -->
    <xsl:template match="dtbook:q">
        <q>
            <xsl:call-template name="attlist.q"/>
            <xsl:apply-templates select="node()"/>
        </q>
    </xsl:template>

    <xsl:template name="attlist.q">
        <xsl:call-template name="attrs"/>
        <xsl:copy-of select="@cite"/>
    </xsl:template>

    <xsl:template match="dtbook:img">
        <img>
            <xsl:call-template name="attlist.img"/>
            <xsl:apply-templates select="node()"/>
        </img>
    </xsl:template>

    <xsl:template name="attlist.img">
        <xsl:param name="all-ids" select="()" tunnel="yes"/>
        <xsl:call-template name="attrs">
            <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
        </xsl:call-template>
        <xsl:attribute name="src" select="concat('images/',@src)"/>
        <xsl:copy-of select="@alt|@longdesc|@height|@width"/>
        <xsl:if test="not(@longdesc) and @id">
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="longdesc" select="(//dtbook:prodnote|//dtbook:caption)[tokenize(@imgref,'\s+')=$id]"/>
            <xsl:if test="$longdesc">
                <xsl:attribute name="longdesc" select="concat('#',$longdesc[1]/((@id,f:generate-pretty-id(.,$all-ids))[1]))"/>
                <!-- NOTE: if the image has multiple prodnotes or captions, only the first one will be referenced. -->
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:imggroup">
        <figure>
            <xsl:call-template name="attlist.imggroup"/>

            <xsl:variable name="imggroup-captions" select="dtbook:img[1]/preceding-sibling::dtbook:caption[1]/(. | preceding-sibling::node()[not(self::text())])"/>
            <xsl:variable name="imggroup-trailing-content"
                select="if ($imggroup-captions) then ($imggroup-captions[last()]/following-sibling::node()[not(self::text())] intersect dtbook:img[1]/preceding-sibling::node()[not(self::text())]) else dtbook:img[1]/preceding-sibling::node()[not(self::text())]"/>
            <xsl:choose>
                <xsl:when test="not($imggroup-captions[self::dtbook:caption])">
                    <xsl:apply-templates select="$imggroup-captions"/>
                </xsl:when>
                <xsl:when test="count($imggroup-captions) = 1">
                    <figcaption>
                        <xsl:for-each select="$imggroup-captions[self::*][1]">
                            <xsl:call-template name="attlist.caption"/>
                        </xsl:for-each>
                        <xsl:apply-templates select="$imggroup-captions"/>
                    </figcaption>
                </xsl:when>
                <xsl:when test="count($imggroup-captions) &gt; 1">
                    <figcaption>
                        <xsl:for-each select="$imggroup-captions">
                            <div>
                                <xsl:call-template name="attlist.caption"/>
                                <xsl:apply-templates select="."/>
                            </div>
                        </xsl:for-each>
                    </figcaption>
                </xsl:when>
            </xsl:choose>
            <xsl:apply-templates select="$imggroup-trailing-content"/>

            <xsl:for-each select="dtbook:img">
                <xsl:variable name="captions"
                    select="if (not(following-sibling::dtbook:caption)) then () else following-sibling::node()[not(self::text())] intersect (if (following-sibling::dtbook:img) then (following-sibling::dtbook:img[1]/preceding-sibling::dtbook:caption[1]/(. | preceding-sibling::node()[not(self::text())])) else (following-sibling::dtbook:caption[last()]/(. | preceding-sibling::node()[not(self::text())])))"/>
                <xsl:variable name="trailing-content"
                    select="(if ($captions) then $captions[last()] else .)/following-sibling::node()[not(self::text())] intersect (if (following-sibling::dtbook:img) then following-sibling::dtbook:img[1]/preceding-sibling::node()[not(self::text())] else following-sibling::node())"/>
                <figure class="image">
                    <xsl:apply-templates select="."/>
                    <xsl:choose>
                        <xsl:when test="count($captions[self::*]) = 1">
                            <figcaption>
                                <xsl:for-each select="$captions">
                                    <xsl:call-template name="attlist.caption"/>
                                    <xsl:apply-templates select="."/>
                                </xsl:for-each>
                            </figcaption>
                        </xsl:when>
                        <xsl:when test="count($captions[self::*]) &gt; 1">
                            <figcaption>
                                <xsl:for-each select="$captions">
                                    <xsl:choose>
                                        <xsl:when test="self::dtbook:caption">
                                            <div>
                                                <xsl:call-template name="attlist.caption"/>
                                                <xsl:apply-templates select="."/>
                                            </div>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:apply-templates select="."/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:for-each>
                            </figcaption>
                        </xsl:when>
                    </xsl:choose>
                </figure>
                <xsl:apply-templates select="$trailing-content"/>
            </xsl:for-each>
        </figure>
    </xsl:template>

    <xsl:template name="attlist.imggroup">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'image-series'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:p">
        <xsl:variable name="element" select="."/>
        <xsl:variable name="has-block-elements" select="if (dtbook:list or dtbook:dl or dtbook:imggroup) then true() else false()"/>
        <xsl:if test="f:classes($element)=('precedingemptyline')">
            <hr/>
        </xsl:if>
        <xsl:element name="{if ($has-block-elements) then 'div' else 'p'}" namespace="http://www.w3.org/1999/xhtml">
            <!-- div allows the same attributes as p -->
            <xsl:call-template name="attlist.p">
                <xsl:with-param name="except-classes" select="'precedingemptyline'" tunnel="yes"/>
            </xsl:call-template>
            <xsl:for-each-group select="node()" group-adjacent="not(self::dtbook:list or self::dtbook:dl or self::dtbook:imggroup)">
                <xsl:choose>
                    <xsl:when test="current-grouping-key()">
                        <xsl:choose>
                            <xsl:when test="$has-block-elements">
                                <p>
                                    <xsl:apply-templates select="current-group()"/>
                                </p>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates select="current-group()"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- In HTML, lists and figures(imggroup) are not allowed inside p. -->
                        <xsl:apply-templates select="current-group()"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each-group>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.p">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:doctitle">
        <xsl:element name="{if (parent::dtbook:frontmatter) then 'h1' else 'p'}">
            <xsl:call-template name="attlist.doctitle"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.doctitle">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'fulltitle'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:docauthor">
        <p>
            <xsl:call-template name="attlist.docauthor"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <xsl:template name="attlist.docauthor">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:author'" tunnel="yes"/>
            <xsl:with-param name="classes" select="'docauthor'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:covertitle">
        <p>
            <xsl:call-template name="attlist.covertitle"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <xsl:template name="attlist.covertitle">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'z3998:covertitle'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:h1 | dtbook:h2 | dtbook:h3 | dtbook:h4 | dtbook:h5 | dtbook:h6 | dtbook:hd">
        <xsl:element name="h{f:level(.)}">
            <xsl:call-template name="attlist.h"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.h">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:bridgehead">
        <p>
            <xsl:call-template name="attlist.bridgehead"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <xsl:template name="attlist.bridgehead">
        <xsl:call-template name="attrs">
            <xsl:with-param name="types" select="'bridgehead'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="dtbook:blockquote">
        <blockquote>
            <xsl:call-template name="attlist.blockquote"/>
            <xsl:apply-templates select="node()"/>
        </blockquote>
    </xsl:template>

    <xsl:template name="attlist.blockquote">
        <xsl:call-template name="attrs"/>
        <xsl:copy-of select="@cite"/>
    </xsl:template>

    <xsl:template match="dtbook:dl">
        <xsl:apply-templates select="node()[self::dtbook:pagenum|self::text()|self::comment()][not(preceding-sibling::*[self::dtbook:dt or self::dtbook:dd])]">
            <xsl:with-param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        </xsl:apply-templates>
        <dl>
            <xsl:call-template name="attlist.dl"/>
            <xsl:apply-templates
                select="dtbook:dt|dtbook:dd | (comment()|text())[preceding-sibling::*[self::dtbook:dt or self::dtbook:dd] and following-sibling::*[self::dtbook:dt or self::dtbook:dd]]"/>
        </dl>
        <xsl:apply-templates
            select="node()[self::dtbook:pagenum|self::text()|self::comment()][preceding-sibling::*[self::dtbook:dt or self::dtbook:dd] and not(following-sibling::*[self::dtbook:dt or self::dtbook:dd])]">
            <xsl:with-param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template name="attlist.dl">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:dt">
        <dt>
            <xsl:call-template name="attlist.dt"/>
            <xsl:apply-templates select="node()"/>
            <xsl:variable name="this" select="."/>
            <xsl:for-each
                select="following-sibling::node()[self::dtbook:pagenum|self::text()|self::comment()][preceding-sibling::*[self::dtbook:dd|self::dtbook:dt][1]=$this][following-sibling::*[self::dtbook:dt or self::dtbook:dd]]">
                <xsl:if test="position()=1">
                    <xsl:text> </xsl:text>
                </xsl:if>
                <xsl:apply-templates select=".">
                    <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
                </xsl:apply-templates>
            </xsl:for-each>
        </dt>
    </xsl:template>

    <xsl:template name="attlist.dt">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:dd">
        <dd>
            <xsl:call-template name="attlist.dd"/>
            <xsl:apply-templates select="node()"/>
            <xsl:variable name="this" select="."/>
            <xsl:for-each
                select="following-sibling::node()[self::dtbook:pagenum|self::text()|self::comment()][preceding-sibling::*[self::dtbook:dd|self::dtbook:dt][1]=$this][following-sibling::*[self::dtbook:dt or self::dtbook:dd]]">
                <xsl:if test="position()=1">
                    <xsl:text> </xsl:text>
                </xsl:if>
                <xsl:apply-templates select=".">
                    <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
                </xsl:apply-templates>
            </xsl:for-each>
        </dd>
    </xsl:template>

    <xsl:template name="attlist.dd">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:list">
        <xsl:param name="all-ids" select="()" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="dtbook:hd">
                <section>
                    <xsl:attribute name="id" select="f:generate-pseudorandom-id(concat(f:generate-pretty-id(.,$all-ids),'_section'),$all-ids)"/>
                    <xsl:call-template name="list.content">
                        <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
                    </xsl:call-template>
                </section>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="list.content">
                    <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="list.content">
        <xsl:apply-templates select="dtbook:pagenum[not(preceding-sibling::dtbook:li)]">
            <xsl:with-param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        </xsl:apply-templates>
        <xsl:element name="{if (@type='ul') then 'ul' else 'ol'}">
            <xsl:call-template name="attlist.list"/>
            <xsl:apply-templates select="dtbook:li|text()|comment()"/>
        </xsl:element>
        <xsl:apply-templates select="dtbook:pagenum[preceding-sibling::dtbook:li and not(following-sibling::dtbook:li)]">
            <xsl:with-param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template name="attlist.list">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="if (@type='pl') then 'list-preformatted' else ()" tunnel="yes"/>
        </xsl:call-template>
        <!-- @depth is implicit; ignore it -->
        <xsl:if test="@enum">
            <xsl:attribute name="type" select="@enum"/>
        </xsl:if>
        <xsl:copy-of select="@start"/>
    </xsl:template>
    
    <xsl:template match="dtbook:li">
        <li>
            <xsl:call-template name="attlist.li"/>
            <xsl:apply-templates select="node()"/>
            <xsl:variable name="this" select="."/>
            <xsl:apply-templates select="following-sibling::dtbook:pagenum[preceding-sibling::dtbook:li[1]=$this][following-sibling::dtbook:li]">
                <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
            </xsl:apply-templates>
        </li>
    </xsl:template>

    <xsl:template name="attlist.li">
        <xsl:call-template name="attrs"/>
    </xsl:template>

    <xsl:template match="dtbook:lic">
        <span>
            <xsl:call-template name="attlist.lic"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <xsl:template name="attlist.lic">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes" select="'lic'" tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="cellhalign">
        <xsl:sequence select="if (@align and not(@align='char')) then concat('text-align: ',@align,';') else ()"/>
        <xsl:if test="@align='char'">
            <xsl:sequence select="'text-align: right;'"/>
            <!--
                NOTE: when align is “char”, we could set "padding" to an integer such that
                the cells align horizontally according to the “char” character. For instance,'
                if one cell contains “1.2” and the cell below it contains “1.25”, then the
                padding for the first cell would be 1 and the padding for the last cell would be 0.
                Unless someone requests this, we'll ignore this for now. The algorithm would be
                padding = {length of longest postfix in column} - {length of postfix in current cell}.
            -->
        </xsl:if>
        <!--<xsl:if test="@char">
            <xsl:attribute name="char" select="@char"/> <!-\- Character -\->
        </xsl:if>
        <xsl:if test="@charoff">
            <xsl:attribute name="charoff" select="@charoff"/> <!-\- Length -\->
        </xsl:if>-->
    </xsl:template>

    <xsl:template name="cellvalign">
        <xsl:sequence select="if (@valign) then concat('vertical-align: ',@valign,';') else ()"/>
    </xsl:template>

    <xsl:template match="dtbook:table">
        <xsl:apply-templates
            select="dtbook:pagenum[not(preceding-sibling::*[self::dtbook:caption or self::dtbook:thead or self::dtbook:tbody or self::dtbook:tr])] | dtbook:tbody[not(preceding-sibling::dtbook:caption or preceding-sibling::dtbook:thead)]/dtbook:pagenum[not(preceding-sibling::dtbook:tr)]">
            <xsl:with-param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        </xsl:apply-templates>

        <table>
            <xsl:call-template name="attlist.table"/>
            <xsl:if test="@summary and not(dtbook:caption)">
                <caption>
                    <p class="table-summary">
                        <xsl:value-of select="string(@summary)"/>
                    </p>
                </caption>
            </xsl:if>
            <xsl:apply-templates select="dtbook:caption/preceding-sibling::comment() | dtbook:caption"/>
            <xsl:if test="dtbook:col">
                <colgroup>
                    <xsl:for-each select="dtbook:col">
                        <xsl:variable name="this" select="."/>
                        <xsl:apply-templates select="preceding-sibling::comment()[following-sibling::*[1]=$this] | $this"/>
                    </xsl:for-each>
                </colgroup>
            </xsl:if>
            <xsl:for-each select="dtbook:colgroup">
                <xsl:variable name="this" select="."/>
                <xsl:apply-templates select="preceding-sibling::comment()[following-sibling::*[1]=$this] | $this"/>
            </xsl:for-each>
            <xsl:variable name="tbody" select="node()[not((following-sibling::*|self::*)[self::dtbook:caption or self::dtbook:col or self::dtbook:colgroup])][not(self::dtbook:pagenum)]"/>
            <xsl:choose>
                <xsl:when test="dtbook:tr">
                    <tbody>
                        <xsl:apply-templates select="$tbody"/>
                    </tbody>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$tbody"/>
                </xsl:otherwise>
            </xsl:choose>
        </table>

        <xsl:apply-templates
            select="dtbook:pagenum[preceding-sibling::*[self::dtbook:caption or self::dtbook:thead or self::dtbook:tbody or self::dtbook:tr] and not(following-sibling::*[self::dtbook:caption or self::dtbook:thead or self::dtbook:tbody or self::dtbook:tr])] | dtbook:tbody/dtbook:pagenum[preceding-sibling::dtbook:tr and not(following-sibling::dtbook:tr)]">
            <xsl:with-param name="pagenum.parent" tunnel="yes" select="parent::*"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template name="attlist.table">
        <xsl:call-template name="attrs">
            <xsl:with-param name="classes"
                select="(
                if (@rules) then concat('table-rules-',@rules) else (),
                if (@frame) then concat('table-frame-',@frame) else ()
                )" tunnel="yes"
            />
        </xsl:call-template>
        <!-- @summary handled the dtbook:table and dtbook:caption templates -->
        <xsl:copy-of select="@border"/>
        <xsl:variable name="style">
            <xsl:if test="@cellspacing">
                <xsl:if test="@width">
                    <xsl:sequence select="concat('width: ',@width,if (not(ends-with(@width,'%'))) then 'px;' else ';')"/>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="@cellspacing=('','0')">
                        <xsl:sequence select="'border-collapse: collapse; border-spacing: 0;'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence select="concat('border-collapse: separate; border-spacing: ',@cellspacing,'px;')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
        <!-- @cellpadding is added to the @style attribute of descendant th and td elements -->
    </xsl:template>

    <xsl:template match="dtbook:caption[parent::dtbook:table]">
        <caption>
            <xsl:call-template name="attlist.caption"/>
            <xsl:if test="parent::dtbook:table[@summary]">
                <p class="table-summary">
                    <xsl:value-of select="string(parent::dtbook:table/@summary)"/>
                </p>
            </xsl:if>
            <xsl:apply-templates select="node()"/>
            <xsl:apply-templates select="following-sibling::dtbook:pagenum[not(preceding-sibling::*[self::dtbook:thead or self::dtbook:tbody or self::dtbook:tr])]">
                <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
            </xsl:apply-templates>
            <xsl:apply-templates select="if (not(following-sibling::dtbook:thead)) then following-sibling::dtbook:tbody/dtbook:pagenum[not(preceding-sibling::dtbook:tr)] else ()">
                <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
            </xsl:apply-templates>
        </caption>
    </xsl:template>

    <xsl:template match="dtbook:caption[parent::dtbook:imggroup]">
        <xsl:apply-templates select="node()"/>
        <!--<div>
            <xsl:call-template name="attlist.caption"/>
            
        </div>-->
    </xsl:template>

    <xsl:template name="attlist.caption">
        <xsl:param name="all-ids" select="()" tunnel="yes"/>
        <xsl:call-template name="attrs">
            <xsl:with-param name="all-ids" select="$all-ids" tunnel="yes"/>
        </xsl:call-template>
        <!-- @imgref is dropped, the relationship is preserved in the corresponding img/@longdesc -->
        <xsl:if test="not(@id)">
            <xsl:attribute name="id" select="f:generate-pretty-id(.,$all-ids)"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:thead">
        <thead>
            <xsl:call-template name="attlist.thead"/>
            <xsl:apply-templates select="node()"/>
        </thead>
    </xsl:template>

    <xsl:template name="attlist.thead">
        <xsl:call-template name="attrs"/>
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:tfoot">
        <tfoot>
            <xsl:call-template name="attlist.tfoot"/>
            <xsl:apply-templates select="node()"/>
        </tfoot>
    </xsl:template>

    <xsl:template name="attlist.tfoot">
        <xsl:call-template name="attrs"/>
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:tbody">
        <tbody>
            <xsl:call-template name="attlist.tbody"/>
            <xsl:apply-templates select="node()[not(self::dtbook:pagenum)]"/>
        </tbody>
    </xsl:template>

    <xsl:template name="attlist.tbody">
        <xsl:call-template name="attrs"/>
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:colgroup">
        <colgroup>
            <xsl:call-template name="attlist.colgroup"/>
            <xsl:apply-templates select="node()"/>
        </colgroup>
    </xsl:template>

    <xsl:template name="attlist.colgroup">
        <xsl:call-template name="attrs"/>
        <xsl:copy-of select="@span"/>
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
            <xsl:sequence select="if (@width) then concat('width: ',@width, if (not(ends-with(@width,'%'))) then 'px;' else ';') else ()"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:col">
        <col>
            <xsl:call-template name="attlist.col"/>
            <xsl:apply-templates select="node()"/>
        </col>
    </xsl:template>

    <xsl:template name="attlist.col">
        <xsl:call-template name="attrs"/>
        <xsl:copy-of select="@span"/>
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
            <xsl:sequence select="if (@width) then concat('width: ',@width, if (not(ends-with(@width,'%'))) then 'px;' else ';') else ()"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:tr">
        <tr>
            <xsl:call-template name="attlist.tr"/>
            <xsl:apply-templates select="node()"/>
        </tr>
    </xsl:template>

    <xsl:template name="attlist.tr">
        <xsl:call-template name="attrs"/>
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dtbook:th | dtbook:td">
        <xsl:element name="{local-name()}">
            <xsl:call-template name="attlist.th.td"/>
            <xsl:apply-templates select="node()"/>
            <xsl:if test="not(following-sibling::dtbook:th or following-sibling::dtbook:td)">
                <xsl:choose>
                    <xsl:when test="parent::dtbook:tr/following-sibling::dtbook:tr">
                        <!-- copy pagenums from between the tr elements -->
                        <xsl:variable name="this" select="."/>
                        <xsl:for-each select="parent::dtbook:tr/following-sibling::dtbook:pagenum[preceding-sibling::dtbook:tr[1]=$this]">
                            <xsl:text> </xsl:text>
                            <xsl:apply-templates select=".">
                                <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
                            </xsl:apply-templates>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:when test="parent::dtbook:tr/parent::dtbook:thead">
                        <!-- if thead => copy trailing pagenums from thead and leading pagenums from tbody -->
                        <xsl:for-each select="parent::dtbook:tr/parent::dtbook:thead/following-sibling::dtbook:pagenum[not(preceding-sibling::dtbook:tr)]">
                            <xsl:text> </xsl:text>
                            <xsl:apply-templates select=".">
                                <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
                            </xsl:apply-templates>
                        </xsl:for-each>
                        <xsl:for-each select="parent::dtbook:tr/parent::dtbook:thead/following-sibling::dtbook:tbody/dtbook:pagenum[not(preceding-sibling::dtbook:tr)]">
                            <xsl:text> </xsl:text>
                            <xsl:apply-templates select=".">
                                <xsl:with-param name="pagenum.parent" tunnel="yes" select="."/>
                            </xsl:apply-templates>
                        </xsl:for-each>
                    </xsl:when>
                </xsl:choose>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attlist.th.td">
        <xsl:call-template name="attrs"/>
        <xsl:copy-of select="@headers|@scope|@rowspan|@colspan"/>
        <!-- @abbr and @axis are ignored as they have no good equivalent in HTML -->
        <xsl:variable name="style">
            <xsl:call-template name="cellhalign"/>
            <xsl:call-template name="cellvalign"/>
            <xsl:variable name="cellpadding" select="ancestor::dtbook:table[1][@cellpadding][1]/@cellpadding"/>
            <xsl:sequence select="if ($cellpadding) then concat('padding: ',$cellpadding,if (not(ends-with($cellpadding,'%'))) then 'px;' else ';') else ()"/>
        </xsl:variable>
        <xsl:variable name="style" select="$style[not(.='')]"/>
        <xsl:if test="count($style)">
            <xsl:attribute name="style" select="string-join($style,' ')"/>
        </xsl:if>
    </xsl:template>

    <xsl:function name="f:classes" as="xs:string*">
        <xsl:param name="element" as="element()"/>
        <xsl:sequence select="tokenize($element/@class,'\s+')"/>
    </xsl:function>

    <xsl:function name="f:level" as="xs:integer">
        <xsl:param name="element" as="element()"/>
        <xsl:variable name="level"
            select="count($element/ancestor-or-self::*[self::dtbook:level or self::dtbook:level1 or self::dtbook:level2 or self::dtbook:level3 or self::dtbook:level4 or self::dtbook:level5 or self::dtbook:level6 or self::dtbook:linegroup[dtbook:hd] or self::dtbook:poem[dtbook:hd]])"/>
        <xsl:sequence select="min(($level, 6))"/>
    </xsl:function>

    <xsl:function name="f:generate-pretty-id" as="xs:string">
        <xsl:param name="element" as="element()"/>
        <xsl:param name="all-ids"/>
        <xsl:variable name="id">
            <xsl:choose>
                <xsl:when test="$element[self::dtbook:blockquote or self::dtbook:q]">
                    <xsl:sequence select="concat('quote_',count($element/(ancestor::*|preceding::*)[self::dtbook:blockquote or self::dtbook:q])+1)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="element-name" select="local-name($element)"/>
                    <xsl:sequence select="concat($element-name,'_',count($element/(ancestor::*|preceding::*)[local-name()=$element-name])+1)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:sequence select="if ($all-ids=$id) then generate-id($element) else $id"/>
    </xsl:function>

    <xsl:function name="f:generate-pseudorandom-id" as="xs:string">
        <xsl:param name="prefix" as="xs:string"/>
        <xsl:param name="all-ids"/>
        <xsl:variable name="pseudorandom-id" select="concat($prefix,'_',replace(replace(string(current-time()),'\d+:\d+:([\d\.]+)(\+.*)?','$1'),'[^\d]',''))"/>
        <xsl:choose>
            <xsl:when test="$pseudorandom-id=$all-ids">
                <!--
                    Try again.
                    WARNING: this is a theoretically infinite recursion. In practice however, this shouldn't happen.
                -->
                <xsl:sequence select="f:generate-pseudorandom-id($prefix, $all-ids)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="$pseudorandom-id"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="f:is-inline" as="xs:boolean">
        <xsl:param name="parent" as="element()?"/>
        <xsl:choose>
            <xsl:when test="$parent">
                <xsl:variable name="sibling-implies-inline"
                    select="('em','strong','dfn','code','samp','kbd','cite','abbr','acronym','a','img','br','q','sub','sup','span','bdo','sent','w','annoref','noteref','lic')"/>
                <xsl:variable name="parent-implies-inline"
                    select="($sibling-implies-inline,'imggroup','pagenum','prodnote','line','linenum','address','title','author','byline','dateline','p','doctitle','docauthor','covertitle','h1','h2','h3','h4','h5','h6','bridgehead','dt')"/>
                <xsl:sequence
                    select="if ($parent[self::dtbook:* and local-name()=$parent-implies-inline] or $parent/(text()[normalize-space()],dtbook:*[local-name()=$sibling-implies-inline])) then true() else false()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="false()"/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:function>

</xsl:stylesheet>