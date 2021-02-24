import { Injectable } from '@angular/core';
import { InMemoryDbService } from 'angular-in-memory-web-api';
import { Release } from './release';

@Injectable({
  providedIn: 'root',
})
export class InMemoryDataService implements InMemoryDbService {
  createDb() {
    const releases = [
      { id: 0, name: 'Hello', components: ["Hello component 1", "Hello component 2"] },
      { id: 1, name: 'World', components: ["World component 1", "World component 2"] },
      { id: 2, name: 'Mock', components: ["Mock component 1", "Mock component 2"] },
      { id: 3, name: 'Data', components: ["Data component 1", "Data component 2"] }
    ];
    return {releases};
  }

  genId(releases: Release[]): number {
    return releases.length > 0 ? Math.max(...releases.map(release => release.id)) + 1 : 11;
   }
}