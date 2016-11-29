# Base on the image that already has gradle and java
FROM frekele/gradle:3.2.1-jdk8

# Pedro Cuadra
MAINTAINER pedro.cuadrachamorro001@stud.fh-dortmund.de

# Update repos
RUN apt-get update

# Install other dependencies
RUN apt-get -y install graphviz python-pip

# Install pip packages
RUN pip install sphinx sphinx_rtd_theme sphinxcontrib-plantuml

# Download plantuml
## TODO this is not needed
RUN wget -O plantuml.jar https://sourceforge.net/projects/plantuml/files/plantuml.jar/download
RUN mkdir -p /opt/plantuml && mv plantuml.jar /opt/plantuml/
