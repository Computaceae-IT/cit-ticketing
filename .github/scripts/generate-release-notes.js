const { execSync } = require("child_process");
const path = require("path");

const token = process.env.GITHUB_TOKEN;
const repository = process.env.GITHUB_REPOSITORY;
const tagName = process.env.TAG_NAME;
const releaseId = process.env.RELEASE_ID;

if (!token) {
  throw new Error("GITHUB_TOKEN is required");
}

if (!repository) {
  throw new Error("GITHUB_REPOSITORY is required");
}

if (!tagName) {
  throw new Error("TAG_NAME is required");
}

const [owner, repo] = repository.split("/");
const apiBase = "https://api.github.com";
const maxReleaseBodyLength = 120000;

async function github(pathname, options = {}) {
  const response = await fetch(`${apiBase}${pathname}`, {
    ...options,
    headers: {
      Accept: "application/vnd.github+json",
      Authorization: `Bearer ${token}`,
      "X-GitHub-Api-Version": "2022-11-28",
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    const body = await response.text();
    throw new Error(`${options.method || "GET"} ${pathname} failed with ${response.status}: ${body}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function getTags() {
  const output = execSync("git for-each-ref --sort=-creatordate refs/tags --format=%(refname:short)%09%(creatordate:iso8601-strict)", {
    encoding: "utf8",
  });

  return output
    .trim()
    .split(/\r?\n/)
    .filter(Boolean)
    .map((line) => {
      const [tag, date] = line.split("\t");
      return { tag, date };
    });
}

function getReleaseRange() {
  const tags = getTags();
  const currentIndex = tags.findIndex((tag) => tag.tag === tagName);

  if (currentIndex < 0) {
    throw new Error(`Tag ${tagName} was not found in the checked out repository`);
  }

  const current = tags[currentIndex];
  const previous = tags[currentIndex + 1];

  if (!previous) {
    throw new Error(`No previous tag found before ${tagName}`);
  }

  return { current, previous };
}

async function searchClosedIssues(fromDate, toDate) {
  const issues = [];
  let page = 1;

  while (true) {
    const query = encodeURIComponent(`repo:${repository} is:issue is:closed closed:${fromDate}..${toDate}`);
    const result = await github(`/search/issues?q=${query}&sort=updated&order=desc&per_page=100&page=${page}`);

    issues.push(...result.items);

    if (result.items.length < 100) {
      break;
    }

    page += 1;
  }

  return issues;
}

function toGrenIssue(issue) {
  return {
    text: `#${issue.number}`,
    name: issue.title || "",
    labels: issue.labels || [],
    body: issue.body || "",
    url: issue.html_url,
  };
}

function truncateBody(body) {
  if (body.length <= maxReleaseBodyLength) {
    return body;
  }

  return `${body.substring(0, maxReleaseBodyLength)}

---

Release notes truncated because GitHub release bodies are size-limited.
Full changelog: https://github.com/${repository}/compare/${process.env.PREVIOUS_TAG}...${tagName}
`;
}

async function updateRelease(body) {
  let id = releaseId;

  if (!id) {
    const release = await github(`/repos/${owner}/${repo}/releases/tags/${encodeURIComponent(tagName)}`);
    id = release.id;
  }

  await github(`/repos/${owner}/${repo}/releases/${id}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      name: tagName,
      body,
    }),
  });
}

async function main() {
  const { current, previous } = getReleaseRange();
  process.env.PREVIOUS_TAG = previous.tag;

  const issues = await searchClosedIssues(previous.date, current.date);
  const config = require(path.join(process.cwd(), ".grenrc.js"));

  for (const issue of issues.map(toGrenIssue)) {
    config.template.issue(issue);
  }

  const groupBody = config.template.group({});
  const releaseBody = config.template.release({
    release: tagName,
    body: `${groupBody}

**Full Changelog**: https://github.com/${repository}/compare/${previous.tag}...${tagName}`,
  });

  await updateRelease(truncateBody(releaseBody));
  console.log(`Updated ${tagName} with ${issues.length} closed issue(s) since ${previous.tag}.`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
