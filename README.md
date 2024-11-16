# Analysis of the peer-to-peer vs client-server architectures in a file sharing system


### Introduction
  In today's world, sharing files over networks is a big part of how we communicate and collaborate. Two of the most popular network architectures are the client-server and peer-to-peer (P2P) architecture.
The project proposes to examine both architectures based on a file-sharing system and evaluate their pros and cons. The main technologies used will be Java 21, Spring Boot 3, Thymeleaf and the H2 database. 


### Methodology
  Two versions of a file-sharing system will be implemented:
  
•	Client-server model: There will be a simple client that can upload and download files to and from a central server.

•	Peer-to-peer model: In this model, we will still have a server, but it will store only the file metadata. The clients will upload this data and they will be able to use server’s file metadata to download chunks of a file from multiple other clients.
The technology stack will be the same and we want to have the same set of features on both models in the end.
After the implementation will be completed, a comparison between these two models will be done. It will include the difference in download speed between the two models, comparisons based on resource utilizations, scalability, ease of implementation and other factors.


### Project Model Analysis
Client-Server Architecture
In the client-server model, everything revolves around a central server. Clients can upload files to this server and download them as needed. It's straightforward and easy to understand, but if too many clients use it at once, the server can get overwhelmed. 


### Peer-to-Peer Architecture
The P2P model is a bit more complex. Clients share files directly with each other, and a central server just helps them find each other. This means no single server gets overloaded, and the system can handle more users. However, it also means that the system depends on clients being online and willing to share files.


### Efficiency and Optimization
// This paragraph will be completed after the implementation will be done


### Comparison Study
// This paragraph will be completed after the implementation will be done


### Antipatterns Impact
// This paragraph will be completed after the implementation will be done


### Conclusion
// This paragraph will be completed after the implementation will be done

