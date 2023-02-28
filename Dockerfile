FROM openjdk:8

ENV IDC zeus
ENV MODULE coding
ENV ENVTYPE test
ENV DEBUGPORT 9008
ENV JMXPORT 9009
ENV MATRIX_CODE_DIR /opt/coding/htdocs
ENV MATRIX_APPLOGS_DIR /opt/coding/applogs
ENV MATRIX_ACCESSLOGS_DIR /opt/coding/logs
ENV MATRIX_LOGS_DIR /opt/coding/logs
ENV MATRIX_CACHE_DIR /opt/coding/cache
ENV MATRIX_PRIVDATA_DIR /opt/coding/privdata

COPY release/ /opt/coding/htdocs/
RUN chmod +x /opt/coding/htdocs/bin/*.sh

EXPOSE 8080 9008 9009
WORKDIR /opt/coding/htdocs
VOLUME ["/opt/coding/applogs", "/opt/coding/logs", "/opt/coding/cache", "/opt/coding/privdata"]
CMD ["/bin/bash", "-x", "/opt/coding/htdocs/bin/run.sh"]
