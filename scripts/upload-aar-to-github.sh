#!/bin/bash
# upload-aar-to-github.sh — Upload a third-party AAR to GitHub Packages
#
# Usage:
#   ./upload-aar-to-github.sh <aar-file> <group-id> <artifact-id> <version>
#
# Example:
#   ./upload-aar-to-github.sh tmap-sdk-3.5.aar com.skt.tmap tmap-sdk 3.5
#
# Requirements:
#   - curl
#   - GitHub token with write:packages scope
#   - Environment variables (or set in script):
#       GITHUB_PACKAGES_USER=your_github_username
#       GITHUB_PACKAGES_TOKEN=ghp_xxxxxxxxxxxx
#       GITHUB_PACKAGES_OWNER=github_user_or_org  (default: li-lance)
#       GITHUB_PACKAGES_REPO=github_repo_name     (default: android-seraphim-map)

set -euo pipefail

# ── Parse args ──
AAR_FILE="${1:?Missing AAR file path}"
GROUP_ID="${2:?Missing group ID (e.g. com.skt.tmap)}"
ARTIFACT_ID="${3:?Missing artifact ID (e.g. tmap-sdk)}"
VERSION="${4:?Missing version (e.g. 3.5)}"

if [ ! -f "$AAR_FILE" ]; then
    echo "ERROR: AAR file not found: $AAR_FILE"
    exit 1
fi

# ── GitHub Packages config ──
GITHUB_USER="${GITHUB_PACKAGES_USER:-}"
GITHUB_TOKEN="${GITHUB_PACKAGES_TOKEN:-}"
GITHUB_OWNER="${GITHUB_PACKAGES_OWNER:-li-lance}"
GITHUB_REPO="${GITHUB_PACKAGES_REPO:-android-seraphim-map}"

if [ -z "$GITHUB_USER" ] || [ -z "$GITHUB_TOKEN" ]; then
    echo "ERROR: Set GITHUB_PACKAGES_USER and GITHUB_PACKAGES_TOKEN"
    echo "  export GITHUB_PACKAGES_USER=li-lance"
    echo "  export GITHUB_PACKAGES_TOKEN=ghp_xxxx"
    exit 1
fi

# ── Maven paths ──
GROUP_PATH="${GROUP_ID//.//}"
BASE_URL="https://maven.pkg.github.com/${GITHUB_OWNER}/${GITHUB_REPO}"
ARTIFACT_URL="${BASE_URL}/${GROUP_PATH}/${ARTIFACT_ID}/${VERSION}"

AAR_NAME="${ARTIFACT_ID}-${VERSION}.aar"
POM_NAME="${ARTIFACT_ID}-${VERSION}.pom"

echo "=== Uploading AAR to GitHub Packages ==="
echo "  Repo:    ${GITHUB_OWNER}/${GITHUB_REPO}"
echo "  Maven:   ${GROUP_ID}:${ARTIFACT_ID}:${VERSION}"
echo "  File:    ${AAR_FILE}"
echo "  Target:  ${ARTIFACT_URL}/${AAR_NAME}"
echo ""

# ── Create temp directory ──
TMPDIR=$(mktemp -d)
trap "rm -rf $TMPDIR" EXIT

# ── Generate POM ──
cat > "$TMPDIR/$POM_NAME" << POMEOF
<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>
    <groupId>${GROUP_ID}</groupId>
    <artifactId>${ARTIFACT_ID}</artifactId>
    <version>${VERSION}</version>
    <packaging>aar</packaging>
    <name>${ARTIFACT_ID}</name>
    <description>Third-party AAR deployed to GitHub Packages</description>
    <url>https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}</url>
    <licenses>
        <license>
            <name>Proprietary</name>
        </license>
    </licenses>
</project>
POMEOF

# ── Upload function ──
upload() {
    local file="$1"
    local url="$2"
    echo "  Uploading: $(basename "$file") -> $url"
    local http_code
    http_code=$(curl -s -o /dev/null -w "%{http_code}" \
        -u "${GITHUB_USER}:${GITHUB_TOKEN}" \
        -H "Content-Type: application/octet-stream" \
        --upload-file "$file" \
        "$url")
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ] || [ "$http_code" = "409" ]; then
        echo "    OK (HTTP $http_code)"
    else
        echo "    FAILED (HTTP $http_code)"
        return 1
    fi
}

# ── Upload AAR ──
upload "$AAR_FILE" "${ARTIFACT_URL}/${AAR_NAME}" || exit 1

# ── Upload POM ──
upload "$TMPDIR/$POM_NAME" "${ARTIFACT_URL}/${POM_NAME}" || exit 1

# ── Upload checksums (optional but recommended) ──
# MD5
md5 -q "$AAR_FILE" > "$TMPDIR/${AAR_NAME}.md5"
upload "$TMPDIR/${AAR_NAME}.md5" "${ARTIFACT_URL}/${AAR_NAME}.md5" 2>/dev/null || true

# SHA1
shasum -a 1 "$AAR_FILE" | awk '{print $1}' > "$TMPDIR/${AAR_NAME}.sha1"
upload "$TMPDIR/${AAR_NAME}.sha1" "${ARTIFACT_URL}/${AAR_NAME}.sha1" 2>/dev/null || true

echo ""
echo "=== Done ==="
echo "Consumers can now add:"
echo ""
echo "  implementation(\"${GROUP_ID}:${ARTIFACT_ID}:${VERSION}\")"
echo ""
echo "Repository config in settings.gradle.kts:"
echo ""
echo "  maven(\"https://maven.pkg.github.com/${GITHUB_OWNER}/${GITHUB_REPO}\") {"
echo "      credentials {"
echo "          username = providers.gradleProperty(\"GITHUB_PACKAGES_USER\")"
echo "          password = providers.gradleProperty(\"GITHUB_PACKAGES_TOKEN\")"
echo "      }"
echo "  }"
