#
# spec file for package PodCaster %%version%%
#
# Copyright (C) 2008  John-Paul.Stanford <dev@stanwood.org.uk>
# This file and all modifications and additions to the pristine
# package are under the same license as the package itself.
#
# Please submit bugfixes or comments via http://code.google.com/p/pod-caster/
#
Name:           podcaster
Requires:       java >= 1.6
Requires:       jpackage-utils
Requires:       jakarta-commons-cli = 1.0
Requires:       ROME >= 1.0
Requires:       jaudiotagger >= 2.0.2
Requires:       log4j >= 1.2.15
Requires:       jakarta-commons-logging >= 1.0.4
Requires:       joda-time >= 1.6
BuildRequires:  jpackage-utils
BuildRequires:  unzip
BuildRequires:  ROME >= 1.0
BuildRequires:  jakarta-commons-cli = 1.0
BuildRequires:  java >= 1.6
BuildRequires:  java-devel >= 1.6
BuildRequires:  jakarta-commons-logging >= 1.0.4
BuildRequires:  log4j >= 1.2.15
BuildRequires:  ant
BuildRequires:  ant-trax
BuildRequires:  xalan-j2
BuildRequires:  jaudiotagger >= 2.0.2
BuildRequires:  xmlgraphics-fop >= 0.95
BuildRequires:  excalibur-avalon-framework
BuildRequires:  docbook-xsl-stylesheets
BuildRequires:  docbook_4
BuildRequires:  libxslt1 >= 1.1.26
BuildRequires:  joda-time >= 1.6
BuildRequires:  xsltproc
Summary:        A application used to create pod casts of radio shows
Version:        %%version%%
Release:        1
License:        GPL
Group:          Applications/Internet
URL:            http://code.google.com/p/pod-caster/
Source:         http://pod-caster.googlecode.com/files/%%sourcefile%%
BuildRoot:      %{_tmppath}/%{name}-%{version}-build
BuildArch:      noarch

%description
%%description%%

Authors
-------
  John-Paul Stanford <dev@stanwood.org.uk>

%package javadoc
Summary:    Javadoc for podcaster
Group:      Documentation/HTML
PreReq:     coreutils

%description javadoc
Javadoc for podcaster application and API.

%prep
%setup -q

%build
export CLASSPATH=$CLASSPATH:/usr/share/java/xalan-j2-serializer.jar
%ant -buildfile opensuse-build.xml \
     -Dlib.dir=%{_javadir} \
     -Dproject.version=%{version} \
     -Ddocbook.home=/usr/share/xml/docbook/stylesheet/nwalsh/current \
     -Dfop.dir=/usr/share/java all

%install
export NO_BRP_CHECK_BYTECODE_VERSION=true
%__install -dm 755 %{buildroot}%{_javadir}
%__install -dm 755 %{buildroot}%{_bindir}
%__install -dm 755 %{buildroot}/etc
%__install -m 644 dist/%{name}-%{version}.jar %{buildroot}%{_javadir}/%{name}-%{version}.jar
pushd %{buildroot}%{_javadir}
    for jar in *-%{version}*; do
        ln -sf ${jar} `echo $jar| sed "s|-%{version}||g"`
    done
popd
%__install -m 755 build/scripts/opensuse/capture-stream %{buildroot}%{_bindir}/capture-stream
%__install -m 755 build/scripts/opensuse/podcaster %{buildroot}%{_bindir}/podcaster
%__install -m 755 build/scripts/opensuse/podcaster-java %{buildroot}%{_bindir}/podcaster-java
%__install -m 644 etc/defaultConfig.xml %{buildroot}/etc/podcaster-conf.xml  

# User docs
%__install -dm 755 %{buildroot}/usr/share/doc/%{name}
%__install -m 644 docs/userguide/podcaster-userguide-%{version}.pdf %{buildroot}/usr/share/doc/%{name}/userguide.pdf
%__install -m 644 docs/userguide/html/podcaster-userguide-%{version}.html %{buildroot}/usr/share/doc/%{name}/userguide.html
%__install -dm 755 %{buildroot}%{_datadir}/applications
%__install -m 644 build/shortcuts/PodCaster\ Documentation\ \(HTML\).desktop %{buildroot}%{_datadir}/applications/
%__install -m 644 build/shortcuts/PodCaster\ Documentation\ \(PDF\).desktop %{buildroot}%{_datadir}/applications/

# javadoc
%__install -dm 755 %{buildroot}%{_javadocdir}/%{name}-%{version}
%__cp -pr docs/api/* %{buildroot}%{_javadocdir}/%{name}-%{version}
ln -s %{name}-%{version} %{buildroot}%{_javadocdir}/%{name} # ghost symlink

%clean
[ -d "%{buildroot}" -a "%{buildroot}" != "" ] && %__rm -rf "%{buildroot}"

%post javadoc
%__rm -f %{_javadocdir}/%{name}
ln -s %{name}-%{version} %{_javadocdir}/%{name}

%files
%defattr(-,root,root)
%{_javadir}/*.jar
%{_bindir}/capture-stream
%{_bindir}/podcaster
%{_bindir}/podcaster-java
%dir /usr/share/doc/%{name}
%doc /usr/share/doc/%{name}/*
%config /etc/podcaster-conf.xml 

%files javadoc
%defattr(-,root,root)
%doc %{_javadocdir}/%{name}-%{version}
%ghost %doc %{_javadocdir}/%{name}

%changelog
%%changelog%%
