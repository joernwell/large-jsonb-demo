# Eclipse IDE Configuration Templates

This folder contains configuration templates for Eclipse IDE, including settings for code cleanup, code formatting, and import order. Follow the steps below to import these settings into your Eclipse IDE.

## Files

- `cleanup.xml`: Code cleanup configuration.
- `formatter.xml`: Code formatter configuration.
- `import.importorder`: Import order configuration.

## Importing the Configuration Files

### 1. Code Formatter

1. Open Eclipse and go to **Window** > **Preferences**.
2. Navigate to **Java** > **Code Style** > **Formatter**.
3. Click on **Import...**.
4. Select `formatter.xml` from the `ide-templates/eclipse` folder.
5. Click **Apply and Close** to save the changes.

### 2. Code Cleanup

1. In Eclipse, go to **Window** > **Preferences**.
2. Navigate to **Java** > **Code Style** > **Clean Up**.
3. Click on **Import...**.
4. Select `cleanup.xml` from the `ide-templates/eclipse` folder.
5. Click **Apply and Close** to save the changes.

### 3. Import Order

1. Go to **Window** > **Preferences** in Eclipse.
2. Navigate to **Java** > **Code Style** > **Organize Imports**.
3. In the **Import Order** section, click **Import...**.
4. Select `import.importorder` from the `ide-templates/eclipse` folder.
5. Click **Apply and Close** to save the changes.

## Notes

- Ensure that these settings are saved under your project-specific preferences if you want them to be project-specific.
- After importing, these configurations will be applied automatically when you format, clean up, or organize imports in your code.

With these configurations in place, Eclipse will follow the imported formatting, cleanup, and import order rules, helping maintain consistency across your codebase.