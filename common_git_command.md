# Command git command

- github repository address: ASS05
  - `https://github.com/simonangel-fong/ITC-5201-Java-Assignment05.git`

**Forks the above Repository**

## 1. create directory

1. Create an empty directory for this assignment with any directory name.
2. Open vscode -> In vscode, open the above directory.

---

## 2. Clone github repository

- In terminal, launch a new git bash terminal. Be sure the path in git bash terminal is the above directory.

- Clone github repository to the local directory, `<url>`is the url in your repository forked on Gighub.
  - `git clone <url> .`

---

## 3. Configuration the remote repository

- Add remote url
  - `git remote add ass04 <url>`

---

## 4. Create a new branch

- list branch

  - `git branch`

- creat a new branch
  - `git branch <new_branch_name>`
- Switch to the new branch
  - `git checkout <new_branch_name>`

---

## 5. add files and progam in the new branch

not git command needed this stage.

---

## 6. Commit new changes to local `git`

- When necessary work finished.

- Track all untracked files

  - `git add *`

- Commit uncommitted files
  - `git commit -m "<msg>"`
    - `<msg>`: a brief description of the commit.

---

## 7. Push the latest commit to github

- `git push ass04`

## 8. Synchronize the local files in master branch with files on github

- Switch to the master branch

  - `git checkout master`

- Pull the master branch on Github to the local master branch to synchronize files.
  - `git pull ass04 master:master`

---

## 8. New iteration: programming based on the synchoronized files.

---

[Top](#command-git-command)
