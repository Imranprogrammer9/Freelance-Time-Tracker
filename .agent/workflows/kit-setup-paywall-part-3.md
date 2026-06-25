---
description: Continuation of kit-setup-paywall (part 3)
---

This continues `/kit-setup-paywall` sub-step 2.6 — connecting Google Play to
RevenueCat (service-account, automatic or manual) and the RevenueCat product →
entitlement → offering → paywall chain. Do pieces 1–3 in part-2 first.

4. **Service-account JSON → RevenueCat** (the actual Play ↔ RevenueCat connection;
   starts the ~36 h clock) — the fiddliest piece. The Google Cloud half (creating
   the service account, enabling APIs, granting roles, downloading the key) can be
   **done for the developer by the kit** using the `gcloud` CLI, OR walked manually
   in the web console. Ask the user (wait for their answer):

   - **Automatic (recommended)** — the kit installs/uses `gcloud` on your machine,
     you sign in once, and it creates the service account + key for you. You only do
     two clicks at the end (grant Play access + upload to RevenueCat).
   - **Manual** — you do every Google Cloud step yourself in the web console.

   Both paths finish with the same **4B** (Play Console access) + **4C** (upload to
   RevenueCat). Walk whichever branch they pick **paced — STOP and wait** between
   sub-steps. (Official guide:
   https://www.revenuecat.com/docs/service-credentials/creating-play-service-credentials)

   ---

   ### If "Automatic" — kit provisions via `gcloud` (walk 4-Auto.1 → 4-Auto.4, paced)

   This is RevenueCat's official automation script run **locally by the agent**
   (instead of in Google Cloud Shell). It also enables the APIs + grants the roles
   the manual web steps often miss, so the connection is more likely to verify.

   **4-Auto.1 — Ensure `gcloud` is installed.** Run `gcloud --version`. If it's
   missing, tell the developer you'll install the Google Cloud CLI and **wait for
   "yes"**, then install for their OS (detect with `uname`):
   - **macOS:** `brew install --cask google-cloud-sdk`
   - **Linux:** `curl https://sdk.cloud.google.com | bash` then restart the shell
   - **Windows:** download + run
     https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe

   Re-run `gcloud --version` to confirm before continuing.

   **4-Auto.2 — Sign in with the RIGHT account.** Run `gcloud auth login` (it opens
   a browser). Show verbatim, then wait for them to finish:

   > Sign in with the **exact same Google account that owns your Google Play Console
   > and your Google Cloud project.** The wrong account creates the service account
   > in the wrong project — billing then silently fails. Finish the browser sign-in,
   > then say "done".

   **4-Auto.3 — Pick the Google Cloud project.** Work out the `PROJECT_ID`:
   - **If `app/google-services.json` exists** (Firebase is already set up): read
     `project_info.project_id` from it — a Firebase project **is** a Google Cloud
     project. Confirm: "Use your Firebase project `<id>` for this? (recommended —
     keeps everything in one project)." Use it on yes.
   - **Otherwise:** run `gcloud projects list`, show the developer the project IDs,
     and ask which one is linked to their Play account (or to create/pick one).

   Then run `gcloud config set project <PROJECT_ID>`.

   **4-Auto.4 — Provision (confirm, then run).** Tell the developer you'll now create
   the service account, enable APIs, grant roles, and download the key — and **wait
   for "yes"**. Then run these yourself (substitute `<PROJECT_ID>` literally; the
   developer types nothing):

       gcloud services enable cloudresourcemanager.googleapis.com iam.googleapis.com \
         androidpublisher.googleapis.com playdeveloperreporting.googleapis.com \
         pubsub.googleapis.com
       gcloud iam service-accounts create revenuecat-service-account \
         --description="Service account for RevenueCat integration" \
         --display-name="RevenueCat Service Account"
       # service account needs ~30s to propagate before the role bindings work:
       sleep 30
       gcloud projects add-iam-policy-binding <PROJECT_ID> \
         --member="serviceAccount:revenuecat-service-account@<PROJECT_ID>.iam.gserviceaccount.com" \
         --role="roles/pubsub.editor"
       gcloud projects add-iam-policy-binding <PROJECT_ID> \
         --member="serviceAccount:revenuecat-service-account@<PROJECT_ID>.iam.gserviceaccount.com" \
         --role="roles/monitoring.viewer"
       gcloud iam service-accounts keys create revenuecat-key.json \
         --iam-account="revenuecat-service-account@<PROJECT_ID>.iam.gserviceaccount.com"

   If a role binding fails with "service account does not exist", it hasn't
   propagated yet — wait another 30s and retry that command. The key file lands at
   `revenuecat-key.json` in the project root.

   **Protect the key** — it's a credential. Ensure `revenuecat-key.json` is in
   `.gitignore` (add it if missing), and tell the developer to keep it safe / move it
   out of the repo once uploaded.

   Then go to **4B** and **4C** below — the two steps `gcloud` can't do. The
   service-account email is
   `revenuecat-service-account@<PROJECT_ID>.iam.gserviceaccount.com`, and the file to
   upload is `revenuecat-key.json`.

   ---

   ### If "Manual" — walk 4A → 4B → 4C in the web console

   **Do NOT dump it all at once** — walk 4A → 4B → 4C and **STOP and wait for
   "done"** after each one.

   **4A — Create the service account.** Show verbatim, then wait for "done":
   > **Google Cloud Console:**
   > 1. Open https://console.cloud.google.com → pick the project linked to your
   >    Play account.
   > 2. **IAM & Admin → Service Accounts → + Create service account**.
   > 3. Name it (e.g. `revenuecat`) → **Create and continue** → skip the optional
   >    roles → **Done**.
   > 4. Click the new service account → **Keys → Add key → Create new key →
   >    JSON** → download it. **Keep this file safe — it's a credential.**
   > 5. Copy the service account's **email** (`…@<project>.iam.gserviceaccount.com`).

   **4B — Grant it access in Play Console.** Show verbatim, then wait for "done":
   > **Play Console:**
   > 1. https://play.google.com/console → **Users and permissions → Invite new user**.
   > 2. Paste the service-account email (from 4A, or the
   >    `revenuecat-service-account@…` from the automatic path).
   > 3. Grant **View financial data, orders, and cancellation survey responses**
   >    + **Manage orders and subscriptions**.
   > 4. **Invite user / Send invitation**.

   **4C — Upload to RevenueCat.** Show verbatim, then wait for "done":
   > **RevenueCat:**
   > 1. **Apps → your Google Play app**.
   > 2. Upload the JSON key into **Service Account Credentials JSON** → **Save**
   >    (the `revenuecat-key.json` from the automatic path, or the file you
   >    downloaded in 4A).
   >
   > The connection takes **~36 hours** to propagate before purchases validate —
   > that's normal. Keep building meanwhile.
5. **RevenueCat: products → entitlement → offering → paywall** — four linked
   concepts; get the chain right or the paywall stays empty / purchases don't
   unlock. Mental model: a **Product** is the SKU from Play; the **Entitlement**
   (`premium`) is the access flag your app checks; an **Offering** is the set of
   **Packages** the paywall shows; a **Paywall** is the design. **Walk 5A → 5D one
   at a time, STOP and wait for "done"** after each — don't dump all four at once.

   **5A — Import your Play products.** Show verbatim, then wait for "done":
   > **RevenueCat → Product catalog → Products → + New:**
   > - Pick your **Google Play** app as the store.
   > - Paste a **Product ID** exactly as in Play Console (e.g. `premium_monthly`).
   >   For a subscription, RevenueCat reads its base plans in automatically — you
   >   may also pick the base plan (e.g. `monthly`) here.
   > - Repeat for every Product ID you created in piece 3.

   **5B — Attach products to the `premium` entitlement** *(the most-missed step)*.
   Show verbatim, then wait for "done":
   > **RevenueCat → Product catalog → Entitlements → premium → Attach products:**
   > - Add each product you just imported.
   > - This is the switch that flips a buyer to premium — the kit unlocks features
   >   on the `premium` entitlement (`KitConfig.ENTITLEMENT_ID`). Skip it and
   >   purchases go through but **nothing unlocks** in the app.

   **5C — Build the `default` offering.** Show verbatim, then wait for "done":
   > **RevenueCat → Product catalog → Offerings:**
   > - Use the existing `default` offering (or **+ New offering**, identifier
   >   `default`) and make it **current**.
   > - **+ Add package** for each tier → choose a package type (e.g. **Monthly**,
   >   **Annual**) → attach the matching product. The kit shows the **current**
   >   offering, so the one your app displays must be marked current/default.

   **5D — Design + publish the paywall.** Show verbatim, then wait for "done":
   > **RevenueCat → Paywalls → + New:**
   > - Select the `default` offering.
   > - Pick a **template**, set headline / features / button text (prices come from
   >   Play automatically). RevenueCat renders it natively in the app.
   > - **Save**, then **Publish** — a saved-but-unpublished paywall won't appear.
   >
   > Until a build is on a track *and* this paywall is **published**, the device
   > shows "offerings empty" — normal in dev. The kit's `PaywallScreen` loads the
   > published paywall automatically; no code changes when you tweak the design.

## Step 3 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it
builds once at the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Tell the developer real purchases can only be tested with a signed build on a
Play Console test track (and after sub-step 2.6 is complete) — the paywall
screen renders now, but real billing won't credit entitlements until the
Service Account JSON is uploaded. Report what was set.
