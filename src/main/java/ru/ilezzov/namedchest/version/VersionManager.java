package ru.ilezzov.namedchest.version;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.ilezzov.namedchest.BuildConfig;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;
import ru.ilezzov.namedchest.model.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static ru.ilezzov.namedchest.BuildConfig.*;

@RequiredArgsConstructor
public class VersionManager {
    private final PluginLogger logger;
    private final ConsoleMessage consoleMessage;

    @Getter
    private VersionData versionData;
    @Getter
    private VersionType versionType;

    public void loadVersionData() {
        final Response<VersionData> versionDataResponse = this.fetchVersionData();

        if (versionDataResponse.success()) {
            this.versionData = versionDataResponse.data();

            final Response<VersionType> versionTypeResponse = this.identifyVersionType(versionData);

            if (versionTypeResponse.success()) {
                this.versionType = versionTypeResponse.data();
                this.logger.debug(
                        this.consoleMessage.getMessage("version.type.detection.success", versionType)
                );
                return;
            } else {
                this.logger.error(
                        this.consoleMessage.getMessage("version.data.not_loaded"), versionTypeResponse.error()
                );
            }
        } else {
            final Exception e = versionDataResponse.error();

            if (e != null) {
                this.logger.error(
                        this.consoleMessage.getMessage("version.data.not_loaded", versionDataResponse.message()), e
                );
            } else {
                this.logger.error(
                        this.consoleMessage.getMessage("version.data.not_loaded", versionDataResponse.message())
                );
            }
        }
        this.versionType = VersionType.UNREACHABLE;
    }

    private Response<VersionData> fetchVersionData() {
        try {
            this.logger.debug(
                    this.consoleMessage.getMessage("version.data.loading")
            );
            final long start = System.currentTimeMillis();

            final URI uri = URI.create(UPDATE_URL);
            final URL url = uri.toURL();
            final URLConnection connection = url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "ilezzov-code/NamedChest-" + PLUGIN_VERSION);

            try (final InputStream in = connection.getInputStream()) {
                byte[] bytes = in.readAllBytes();
                String content = new String(bytes, StandardCharsets.UTF_8);

                if (!content.trim().startsWith("{")) {
                    return Response.error(
                            this.consoleMessage.getMessage("file.error.syntax", "github returned non-json")
                    );
                }

                final Gson gson = new Gson();
                final VersionData versionDate = gson.fromJson(content, VersionData.class);

                this.logger.debug(
                        this.consoleMessage.getMessage("version.data.loaded", System.currentTimeMillis() - start)
                );
                return Response.ok(versionDate);
            }
        } catch (final UnknownHostException e) {
            return Response.error(
                    this.consoleMessage.getMessage("network.error.no_connection"), e
            );
        } catch (final ConnectException e) {
            return Response.error(
                    this.consoleMessage.getMessage("network.error.connection_rejected"), e
            );
        } catch (SocketTimeoutException e) {
            return Response.error(
                    this.consoleMessage.getMessage("network.error.critical_request", "Connection timed out"), e
            );
        } catch (JsonParseException e) {
            return Response.error(
                    this.consoleMessage.getMessage("file.error.root_structure"), e
            );
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return Response.error(
                        this.consoleMessage.getMessage("network.error.version_not_found"), e
                );
            }
            return Response.error(
                    this.consoleMessage.getMessage("file.error.io", "version.json"), e
            );
        } catch (Exception e) {
            return Response.error(
                    this.consoleMessage.getMessage("network.error.critical_request", e.getMessage(), e)
            );
        }
    }

    private Response<VersionType> identifyVersionType(final VersionData versionData) {
        this.logger.debug(
                this.consoleMessage.getMessage("version.type.detection.start")
        );

        if (versionData == null) {
            return Response.ok(VersionType.UNREACHABLE);
        }

        final String latestVersion = versionData.getLatest().getVersion();
        final String minRequiredVersion = versionData.getCompatibility().getMinRequiredVersion();
        final List<String> blacklistVersions = versionData.getCompatibility().getBlacklistedVersions();

        if (blacklistVersions.contains(PLUGIN_VERSION)) {
            return Response.ok(VersionType.BLACKLIST);
        }

        final Response<Integer> equalsMinRequiredAndCurrent = this.equalsVersion(minRequiredVersion);

        if (!equalsMinRequiredAndCurrent.success()) {
            return Response.error(equalsMinRequiredAndCurrent.message(), equalsMinRequiredAndCurrent.error());
        }

        int equalsStatus = equalsMinRequiredAndCurrent.data();

        if (equalsStatus == -1) {
            return Response.ok(VersionType.OUTDATED);
        }

        final Response<Integer> equalsLatestAndCurrent = this.equalsVersion(latestVersion);

        if (!equalsLatestAndCurrent.success()) {
            return Response.error(equalsLatestAndCurrent.message(), equalsLatestAndCurrent.error());
        }

        equalsStatus = equalsLatestAndCurrent.data();

        if (equalsStatus == -1) {
            return Response.ok(VersionType.SUPPORTED);
        }

        return Response.ok(VersionType.LATEST);
    }

    private Response<Integer> equalsVersion(final String first) {
        final String[] firstSplit = first.split("\\.");
        final String[] secondSplit = BuildConfig.PLUGIN_VERSION.split("\\.");

        final int maxVersionLength = Math.max(firstSplit.length, secondSplit.length);

        for (int i = 0; i < maxVersionLength; i++) {
            try {
                final int num1 = i < firstSplit.length ? Integer.parseInt(firstSplit[i]) : 0;
                final int num2 = i < secondSplit.length ? Integer.parseInt(secondSplit[i]) : 0;

                if (num1 > num2) {
                    return Response.ok(-1);
                }

                if (num1 < num2) {
                    return Response.ok(1);
                }

            } catch (final NumberFormatException e) {
                return Response.error(
                        this.consoleMessage.getMessage("file.error.invalid_version_format"), e
                );
            }
        }
        return Response.ok(0);
    }

}
