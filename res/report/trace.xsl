<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright (C) 2008 The Android Open Source Project Licensed under the 
	Apache License, Version 2.0 (the "License"); you may not use this file except 
	in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
	Unless required by applicable law or agreed to in writing, software distributed 
	under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
	OR CONDITIONS OF ANY KIND, either express or implied. See the License for 
	the specific language governing permissions and limitations under the License. -->

<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="UTF-8"
		indent="yes" />

	<xsl:template match="/">

		<html>
			<head>
				<title>Monkey Trace</title>
				<script>
					function toggle(id) {
					e = document.getElementById(id)
					e.style.display = e.style.display == "none" ? "block" : "none"
					}
				</script>
				<STYLE type="text/css">
					@import "bootstrap.css"
				</STYLE>
			</head>
			<body>
				<h1>Monkey Trace</h1>
					<table class="table table-bordered">
						<tbody>
							<tr>
								<th>Timestamp</th>
								<th>Sequence</th>
								<th>Type</th>
								<th>Message</th>
							</tr>
							<xsl:for-each select="TestResult/MonkeyTest/Event">
								<xsl:sort select="@index" data-type="number" />
								<tr>
								<td>
									<span style="display:inline-block;white-space:nowrap;"><xsl:value-of
									select="@time" /></span></td>
								<td>
									<span style="display:inline-block;white-space:nowrap;"><xsl:value-of
									select="@index" /></span>
								</td>
								<td>
									<span style="display:inline-block;white-space:nowrap;"><span style="color:black"><xsl:value-of
									select="@type" /></span></span></td>
								<td>

									<xsl:if test="@type = 'key'">
									  <span style="display:inline-block;white-space:nowrap;"><xsl:value-of select="@value"/></span>
									</xsl:if>
									<xsl:if test="@type = 'drag'">
										<xsl:for-each select="Touch">
									  		<span style="display:inline-block;white-space:nowrap;">{"X":<xsl:value-of select="@x"/>,"Y":<xsl:value-of select="@y"/>}</span>
									  </xsl:for-each> <!-- end test -->
									</xsl:if>
									<xsl:if test="@type = 'tap'">
									  <span style="display:inline-block;white-space:nowrap;">{"X":<xsl:value-of select="@x"/>,"Y":<xsl:value-of select="@y"/>}</span>
									</xsl:if>
									
								</td>
							</tr>
							</xsl:for-each> <!-- end test -->
							

						</tbody>

					</table>


			</body>
		</html>
	</xsl:template>

	<xsl:template name="filteredResultTestReport">
		<xsl:param name="header" />
		<xsl:param name="resultFilter" />
		<xsl:variable name="numMatching"
			select="count(TestResult/TestPackage/TestSuite//TestCase/Test[@result=$resultFilter])" />
		<xsl:if test="$numMatching &gt; 0">
			<h2 align="center">
				<xsl:value-of select="$header" />
				(
				<xsl:value-of select="$numMatching" />
				)
			</h2>
			<xsl:call-template name="detailedTestReport">
				<xsl:with-param name="resultFilter" select="$resultFilter" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="detailedTestReport">
		<xsl:param name="resultFilter" />
		<DIV>
			<xsl:for-each select="TestResult/TestPackage">
				<xsl:if
					test="$resultFilter=''
                        or count(TestSuite//TestCase/Test[@result=$resultFilter]) &gt; 0">

					<TABLE class="testdetails">
						<TR>
							<TD class="package" colspan="3">
								<xsl:variable name="href">
									<xsl:value-of select="@appPackageName" />
								</xsl:variable>
								<a name="{$href}">
									Compatibility Test Package:
									<xsl:value-of select="@appPackageName" />
								</a>
							</TD>
						</TR>

						<TR>
							<TH width="30%">Test</TH>
							<TH width="5%">Result</TH>
							<TH>Details</TH>
						</TR>

						<!-- test case -->
						<xsl:for-each select="TestSuite//TestCase">

							<xsl:if
								test="$resultFilter='' or count(Test[@result=$resultFilter]) &gt; 0">
								<!-- emit a blank row before every test suite name -->
								<xsl:if test="position()!=1">
									<TR>
										<TD class="testcasespacer" colspan="3"></TD>
									</TR>
								</xsl:if>

								<TR>
									<TD class="testcase" colspan="3">
										<xsl:for-each select="ancestor::TestSuite">
											<xsl:if test="position()!=1">
												.
											</xsl:if>
											<xsl:value-of select="@name" />
										</xsl:for-each>
										<xsl:text>.</xsl:text>
										<xsl:value-of select="@name" />
									</TD>
								</TR>
							</xsl:if>

							<!-- test -->
							<xsl:for-each select="Test">
								<xsl:if test="$resultFilter='' or $resultFilter=@result">
									<TR>
										<TD class="testname">
											--
											<xsl:value-of select="@name" />
										</TD>

										<!-- test results -->
										<xsl:choose>
											<xsl:when test="string(@KnownFailure)">
												<!-- "pass" indicates the that test actually passed (results 
													have been inverted already) -->
												<xsl:if test="@result='pass'">
													<TD class="pass">
														<div
															style="text-align: center; margin-left:auto; margin-right:auto;">
															known problem
														</div>
													</TD>
													<TD class="failuredetails"></TD>
												</xsl:if>

												<!-- "fail" indicates that a known failure actually passed (results 
													have been inverted already) -->
												<xsl:if test="@result='fail'">
													<TD class="failed">
														<div
															style="text-align: center; margin-left:auto; margin-right:auto;">
															<xsl:value-of select="@result" />
														</div>
													</TD>
													<TD class="failuredetails">
														<div class="details">
															A test that was a known failure actually passed. Please
															check.
														</div>
													</TD>
												</xsl:if>
											</xsl:when>

											<xsl:otherwise>
												<xsl:if test="@result='pass'">
													<TD class="pass">
														<div
															style="text-align: center; margin-left:auto; margin-right:auto;">
															<xsl:value-of select="@result" />
														</div>
													</TD>
													<TD class="failuredetails">
														<div class="details">
															<ul>
																<xsl:for-each select="Details/ValueArray/Value">
																	<li>
																		<xsl:value-of select="." />
																	</li>
																</xsl:for-each>
															</ul>
														</div>
													</TD>
												</xsl:if>

												<xsl:if test="@result='fail'">
													<TD class="failed">
														<div
															style="text-align: center; margin-left:auto; margin-right:auto;">
															<xsl:value-of select="@result" />
														</div>
													</TD>
													<TD class="failuredetails">
														<div class="details">
															<xsl:value-of select="FailedScene/@message" />
														</div>
													</TD>
												</xsl:if>

												<xsl:if test="@result='timeout'">
													<TD class="timeout">
														<div
															style="text-align: center; margin-left:auto; margin-right:auto;">
															<xsl:value-of select="@result" />
														</div>
														<TD class="failuredetails"></TD>
													</TD>
												</xsl:if>

												<xsl:if test="@result='notExecuted'">
													<TD class="notExecuted">
														<div
															style="text-align: center; margin-left:auto; margin-right:auto;">
															<xsl:value-of select="@result" />
														</div>
													</TD>
													<TD class="failuredetails"></TD>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>
									</TR> <!-- finished with a row -->
								</xsl:if>
							</xsl:for-each> <!-- end test -->
						</xsl:for-each> <!-- end test case -->
					</TABLE>
				</xsl:if>
			</xsl:for-each> <!-- end test package -->
		</DIV>
	</xsl:template>

	<!-- Take a delimited string and insert line breaks after a some number 
		of elements. -->
	<xsl:template name="formatDelimitedString">
		<xsl:param name="string" />
		<xsl:param name="numTokensPerRow" select="10" />
		<xsl:param name="tokenIndex" select="1" />
		<xsl:if test="$string">
			<!-- Requires the last element to also have a delimiter after it. -->
			<xsl:variable name="token" select="substring-before($string, ';')" />
			<xsl:value-of select="$token" />
			<xsl:text>&#160;</xsl:text>

			<xsl:if test="$tokenIndex mod $numTokensPerRow = 0">
				<br />
			</xsl:if>

			<xsl:call-template name="formatDelimitedString">
				<xsl:with-param name="string" select="substring-after($string, ';')" />
				<xsl:with-param name="numTokensPerRow" select="$numTokensPerRow" />
				<xsl:with-param name="tokenIndex" select="$tokenIndex + 1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
