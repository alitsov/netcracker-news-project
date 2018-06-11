import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, ParamMap} from "@angular/router";
import {ProfileService} from "./profile.service";
import {Profile} from "./profile";
import {Preview} from "../preview";
import {ArticlesService} from "../articles.service";
import {Location} from "@angular/common";
import {Comment} from "../comment";
import {CommentsService} from "../comments.service";
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  private userId: number;
  private profile: Profile;

  previews: Preview[] = [];
  comments: Comment[] = [];

  constructor(private route: ActivatedRoute, private profileService: ProfileService,
              private articlesService: ArticlesService, private commentsService: CommentsService,
              private location: Location, private cdRef:ChangeDetectorRef,
              private authService: AuthService) { }

  private loadArticlesPreview(id: number) {
    this.articlesService.getPreviewByAuthorId(id).subscribe((inPreviews: Preview[]) => {
        this.previews = [...inPreviews];
        console.log(this.previews);
      },
      error => console.log("Error while obtaining all articles: ", error)
    );
  }


  private deleteArticleById(id: number) {
    console.log("removing article...");
    console.log(id);
    this.articlesService.deleteArticleById(id).subscribe((response) => {
      console.log(response);
      window.location.reload();
    });
  }

  private loadComments(id: number) {
    this.commentsService.getCommentsByAuthorId(id).subscribe((inComments: Comment[]) => {
        this.comments = [ ... inComments];
        console.log(this.comments);
      },
      error => console.log("Error while obtaining comments: ", error)
    );
  }

  getRankColor() {
    return "#" + this.profile.rank.color;
  }

  loadProfile(id: number) {
    this.profileService.getProfileById(id).subscribe((profile: Profile) => {
      console.log("get profile", profile);
      this.profile = profile;
    });
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.userId = parseInt(params.get('id'));
      console.log("profile id to load", this.userId);
      this.loadProfile(this.userId);

      console.log("loading previews");
      this.loadArticlesPreview(this.userId);

      console.log("loading comments of 1st article");
      this.loadComments(this.userId);
    });
  }

  canVoteComment(): boolean {
    return this.authService.hasAuthority('OP_VOTE_COMMENT') &&
      this.authService.user.userId != this.userId;
  }

}