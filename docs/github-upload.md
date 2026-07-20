# Upload and Publish on GitHub

## Recommended: GitHub Desktop

1. Extract the downloaded ZIP file.
2. Open GitHub Desktop and choose **File → Add local repository**.
3. Select the extracted `finguard-ai-banking-platform` folder.
4. Choose **Create a repository** when prompted.
5. Use repository name `finguard-ai-banking-platform`, keep it public, and do not add another README or license.
6. Commit the existing files with message `Initial FinGuard AI release`.
7. Click **Publish repository**.
8. Open the repository's **Actions** tab and confirm that CI and CodeQL start.

Do not commit `.env`, API keys, database volumes, `node_modules`, build output, or IDE folders. The supplied `.gitignore` already excludes them.

## Browser upload

The browser uploader is less reliable for a multi-module repository. GitHub Desktop is the better option. When using the browser, upload the extracted project contents—not the ZIP file itself—and preserve the directory structure.

## Enable optional AI briefs safely

For local Docker Compose, put `OPENAI_API_KEY` in your ignored `.env` file. For a hosted deployment, store it in the platform's secret manager. Never paste the key into `application.yml`, Dockerfiles, screenshots, commits, or GitHub issues.

## First verification after publishing

- CI should run Maven verification, the React production build, and Docker Compose configuration validation.
- CodeQL should analyze Java and TypeScript.
- Dependabot will open dependency update pull requests.
- Replace `<your-repository-url>` in the README clone command with your actual repository address.
