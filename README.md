# Quotes
**Quotes** is an Android application where you can **Share**, **Discover** and **Save** New **Quotes!**

## Features
- Endless Scroll like on **Youtube Shorts** or **Instagram Reels!**
- **Share** your **Favorite Quotes**
- **Quotes, Preferences and Collections** are saved in **[Firebase](https://firebase.google.com/)**
- **Account Login, Logout and Username** Creation
- **Create** Your Own **Collections** and Add **Quotes** to them!
- **Search** All Quotes
- Set a **Background** for all **Quotes**

## Features in the Future
- **Quotes** are **Randomized**
- Hundreds of Quotes will be **Added**
- **Request** and **View** all **Your Data** that is stored in **[Firebase](https://firebase.google.com/)**
- Ability to **Delete Your Account** with all data

## What Data Is Being Stored? 
Your Data Is Stored in Two Places:
1. Your Account Info Like Email and Password get Stored in [Firebase Auth](https://firebase.google.com/docs/auth)
2. Everything else gets Stored in [Firebase Realtime Database](https://firebase.google.com/docs/database)

In the Database, data is stored like this:
<details open><summary> root </summary><blockquote>

  <details><summary> Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
    <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>

</blockquote></details>
  <details><summary> Users </summary><blockquote>

  <details><summary> User1 </summary><blockquote>

  <details><summary> Bookmarks </summary><blockquote>

  <details><summary> Collection1 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>

  </blockquote></details>
  <details><summary> Collection2 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Preferences </summary><blockquote>
  
  <details><summary> Background </summary><blockquote>
  
  ~~~
  bgId: rsz_forest_1
  bgQuality: low
  ~~~
 
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>
  </blockquote></details>

  ~~~
  username: username1
  ~~~

  </blockquote></details>

  <details><summary> User2 </summary><blockquote>

  <details><summary> Bookmarks </summary><blockquote>

  <details><summary> Collection1 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>

  </blockquote></details>
  <details><summary> Collection2 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Preferences </summary><blockquote>
  
  <details><summary> Background </summary><blockquote>
  
  ~~~
  bgId: rsz_forest_1
  bgQuality: low
  ~~~
 
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  ~~~
  author: Author1
  quote: Quote1
  user: User1
  ~~~
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  ~~~
  author: Author2
  quote:Quote2
  user: User2
  ~~~
  </blockquote></details>
  </blockquote></details>

  ~~~
  username: username2
  ~~~

  </blockquote></details>
  </blockquote></details>
