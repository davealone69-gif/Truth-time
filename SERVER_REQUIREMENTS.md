# Server requirements and live health checks

## Service matrix

| Service | Requirement | Endpoint / detection | Start or recover |
|---|---|---|---|
| Core Android app and local Room-backed features | Required; no external server is established as mandatory for the whole app | Android process and local database checks | No remote server to start for offline/local features. |
| Google Gemini API | Optional/feature-specific; required only for features explicitly configured to use Gemini | Google Generative Language HTTPS API; verify configured key and a supported model request | Configure a valid key using local ignored configuration or protected CI/server secrets. Do not commit API keys or embed private keys in a distributable APK. |
| Other AI provider or remote backend | Only if an enabled feature explicitly selects it | Use the exact configured HTTPS endpoint and perform a live request | Configure provider credentials securely and verify the endpoint, model and permissions. Do not assume an endpoint exists merely because a client class exists. |
| Build / CI service | Required to execute the documented CI pipeline, not to run the installed app | GitHub Actions workflow run and artifact results | Push/PR/tag as documented and inspect the actual workflow result. A workflow definition is not proof that CI has run. |

## Health-state contract

For each service exposed in Settings, report the result of a live probe rather than whether a key or URL is merely present.

- **HEALTHY**: real request succeeds for the required feature.
- **DEGRADED**: endpoint responds but required model/capability is unavailable.
- **STOPPED**: local service refuses connection.
- **UNREACHABLE**: timeout, DNS or routing failure.
- **AUTH REQUIRED**: provider returns an authentication/permission failure.
- **NOT CONFIGURED**: optional provider has no credentials or endpoint configured.
- **UNVERIFIED**: no successful live probe has run.

Show checked-at time, service/endpoint, safe error text, and an actionable retry/configure instruction. Redact keys and authorization headers. Never mark an API healthy from a non-empty key alone.

## Startup guidance

- Gemini: configure a valid API key in the supported settings flow; internet access is required. No local Gemini server is needed.
- Self-hosted endpoint: start the server on the host, set its reachable HTTPS/LAN endpoint, then run a live connection and capability test.
- Offline features: should remain available when optional cloud AI is unconfigured or unreachable.

## Truth boundary

Repository documentation lists Gemini-related client tests, but does not prove that a live Gemini key is configured or that the provider can currently be reached. Real-device health and remote credentials remain UNVERIFIED until tested in the running app.
