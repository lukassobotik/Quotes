<div align="center"><img style="align:center;" src="./app/src/rsz_logo.png" alt="Logo" width="100" /></div>
<h1 align="center">myQuotes</h1>
<h3 align="center">Share, Discover and Save New Quotes!</h3>
<p align="center">
<a href="https://play.google.com/store/apps/details?id=com.sforge.quotes">Play Store</a> | <a href="https://www.buymeacoffee.com/lukassobotik">Support Me</a> | <a href="https://lukassobotik.dev/project/myQuotes">Website</a> | <a href="https://github.com/lukassobotik/Quotes/blob/master/PrivacyPolicy.md">Privacy Policy</a> | <a href="https://github.com/lukassobotik/Quotes/blob/master/LICENSE">License</a>
</p>

## Overview
If you're looking for inspiration, the app has around 1500 quotes waiting for you to discover. You can easily browse through them with an endless scroll feature, similar to Youtube Shorts or Instagram Reels. When you find a quote you love, you can share it with others, so they can enjoy it too.

All the quotes you save, along with your preferences and collections, are saved in Firebase. This means you can access them from anywhere, at any time. You can also request and view all your data that is stored in Firebase, so you always know exactly what information is being saved.

To get started, you'll need to create an account, which is easy to do with the app's account login, logout, and username creation features. If you ever want to delete your account, the app allows you to do that too, along with all your saved data.

One of the best features of the app is the ability to create your own collections and add quotes to them. This way, you can organize your favorite quotes however you like, making them easier to find and share.

If you're looking for a specific quote, the app's search function allows you to find it quickly and easily. You can even set a background for all the quotes, so you can customize the app to your liking.

Overall, the app is perfect for anyone looking for inspiration or a quick pick-me-up. With its vast collection of quotes and easy-to-use features, you'll be sure to find something that speaks to you.

## What Data Is Being Stored? 
Your Data Is Stored in Two Places:
1. Your Account Info (Email and Password) gets Stored with [Firebase Auth](https://firebase.google.com/docs/auth)
2. Everything else gets Stored in [Firebase Realtime Database](https://firebase.google.com/docs/database)

In the Database, data is stored like this:
<details open><summary> root </summary><blockquote>

  <details><summary> Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
    <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>

  </blockquote></details>
  <details><summary> Users </summary><blockquote>

  <details><summary> User1 </summary><blockquote>

  <details><summary> Bookmarks </summary><blockquote>

  <details><summary> Collection1 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>

  </blockquote></details>
  <details><summary> Collection2 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Preferences </summary><blockquote>
  
  <details><summary> Background </summary><blockquote>
  
  
  bgId: rsz_forest_1

  bgQuality: low
  
 
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>

  
  username: username1
  

  </blockquote></details>

  <details><summary> User2 </summary><blockquote>

  <details><summary> Bookmarks </summary><blockquote>

  <details><summary> Collection1 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>

  </blockquote></details>
  <details><summary> Collection2 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Preferences </summary><blockquote>
  
  <details><summary> Background </summary><blockquote>
  
  
  bgId: rsz_forest_1

  bgQuality: low
  
 
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>

  
  username: username2
  

  </blockquote></details>
  </blockquote></details>
</blockquote></details>

## Screenshots
<div align="center">
    <img style="align:center;" src="./app/src/sc-1.png" alt="Screenshot" width="100" />
    <img style="align:center;" src="./app/src/sc-2.png" alt="Screenshot" width="100" />
    <img style="align:center;" src="./app/src/sc-3.png" alt="Screenshot" width="100" />
    <img style="align:center;" src="./app/src/sc-4.png" alt="Screenshot" width="100" />
    <img style="align:center;" src="./app/src/sc-5.png" alt="Screenshot" width="100" />
</div>