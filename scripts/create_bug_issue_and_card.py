import os
import requests
import yaml

REPO = "Bumbac"
OWNER = "dema28"
PROJECT_ID = "PVT_kwHOCjPm584A5Fy2"
STATUS_FIELD_ID = "PVTSSF_lAHOCjPm584A5Fy2zgt8rEs"
BUGS_OPTION_ID = "930de63c"

GH_TOKEN = os.environ.get("GH_PROJECT_TOKEN")
HEADERS = {
    "Authorization": f"Bearer {GH_TOKEN}",
    "Accept": "application/vnd.github+json"
}
GRAPHQL_HEADERS = {
    "Authorization": f"Bearer {GH_TOKEN}",
    "Content-Type": "application/json"
}

def parse_yaml_block(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        lines = f.readlines()
    if not (lines[0].startswith("<!--") and "---" in lines[1]):
        return None
    yaml_lines = []
    inside = False
    for line in lines:
        if "---" in line:
            inside = not inside
            continue
        if inside:
            yaml_lines.append(line)
    return yaml.safe_load("".join(yaml_lines))

def issue_already_exists(title, issue_id):
    url = f"https://api.github.com/repos/{OWNER}/{REPO}/issues"
    params = {"state": "all", "per_page": 100}
    response = requests.get(url, headers=HEADERS, params=params)
    if response.status_code != 200:
        print("❌ Не удалось получить список issue.")
        return False
    issues = response.json()
    for issue in issues:
        if issue["title"].startswith(issue_id) or (issue_id and f"ID: `{issue_id}`" in issue.get("body", "")):
            print(f"⚠️ Пропущено: issue уже существует — title='{issue['title']}'")
            return True
    return False

def create_issue(title, body):
    url = f"https://api.github.com/repos/{OWNER}/{REPO}/issues"
    data = {"title": title, "body": body, "labels": ["bug"]}
    response = requests.post(url, headers=HEADERS, json=data)
    if response.status_code == 201:
        print("✅ Issue создан")
        return response.json()["node_id"]
    else:
        print(f"❌ Ошибка создания issue: {response.status_code}")
        print(response.text)
        return None

def add_issue_to_project(content_node_id):
    query = """
    mutation AddItem {
      addProjectV2ItemById(input: {
        projectId: "%s",
        contentId: "%s"
      }) {
        item {
          id
        }
      }
    }
    """ % (PROJECT_ID, content_node_id)

    response = requests.post("https://api.github.com/graphql", headers=GRAPHQL_HEADERS, json={"query": query})
    if response.status_code == 200:
        item_id = response.json()["data"]["addProjectV2ItemById"]["item"]["id"]
        print("✅ Карточка добавлена в Project.")
        set_project_field(item_id)
    else:
        print("❌ Ошибка добавления карточки:")
        print(response.text)

def set_project_field(item_id):
    query = """
    mutation SetStatus {
      updateProjectV2ItemFieldValue(input: {
        projectId: "%s",
        itemId: "%s",
        fieldId: "%s",
        value: {
          singleSelectOptionId: "%s"
        }
      }) {
        projectV2Item {
          id
        }
      }
    }
    """ % (PROJECT_ID, item_id, STATUS_FIELD_ID, BUGS_OPTION_ID)

    response = requests.post("https://api.github.com/graphql", headers=GRAPHQL_HEADERS, json={"query": query})
    if response.status_code == 200:
        print("✅ Статус 'BUGS' установлен.")
    else:
        print("❌ Ошибка при установке колонки:")
        print(response.text)

def run_bug_sync():
    folder = "bug_reports"
    for filename in os.listdir(folder):
        if not filename.endswith(".md"):
            continue
        filepath = os.path.join(folder, filename)
        metadata = parse_yaml_block(filepath)
        if not metadata:
            print(f"⚠️ Пропущено (нет YAML): {filename}")
            continue
        issue_id = metadata.get("id")
        title = f"{issue_id} — {metadata.get('title')}"

        if issue_already_exists(title, issue_id):
            continue

        with open(filepath, "r", encoding="utf-8") as f:
            body = f.read()  # 💥 Весь .md как тело issue

        print(f"Создаём issue с title: {title}")
        node_id = create_issue(title, body)
        if node_id:
            add_issue_to_project(node_id)

if __name__ == "__main__":
    run_bug_sync()
