'use strict'; // for ES6 in node

const gulp = require('gulp');
const source = require('vinyl-source-stream');
const browserify = require('browserify');
const reactify = require('reactify');
const watchify = require('watchify');
const resolutions = require('browserify-resolutions');
const eslint = require('gulp-eslint');
const path = require('path');
const sass = require('gulp-sass');
const clean = require('gulp-clean');

const SASS_FILES = './sass/**/*.scss';
const JS_FILES = [
  './gulpfile.js'
];
const BOOTSTRAP_DIR = './node_modules/bootstrap-sass/';

let tinylr;
gulp.task('livereload:init', () => {
  tinylr = require('tiny-lr')();
  tinylr.listen(35729);
});

const notifyLiveReload = (event) => {
  tinylr.changed({
    body: {
      files: [path.relative(__dirname, event.path)]
    }
  });
};

// browserify bundler/watcher, for use in tasks
const bundler = browserify({
  entries: ['./js/main.jsx'],
  paths: ['./node_modules','./js'],
  transform: [reactify],
  debug: true,
  // Required properties for watchify
  cache: {}, packageCache: {}, fullPaths: true
}).plugin(resolutions, '*')
  .on('time', (time) => {
    console.log('Bundle updated in ' + (time / 1000) + 's.');
  });

const watcher = watchify(bundler);

const buildJS = () => {
  watcher
    .bundle()
    .on('error', (err) => {
      console.log(err.toString());
    })
    .pipe(source('main.js'))
    .pipe(gulp.dest('./build/js'));
};

gulp.task('browserify', buildJS);

gulp.task('sass', () => {
  return gulp.src('./sass/*.scss')
  .pipe(sass({
    outputStyle: 'expanded',
    includePaths: [path.join(BOOTSTRAP_DIR, 'assets/stylesheets')]
  })
  .on('error', sass.logError))
  .pipe(gulp.dest('./build/css'));
});

gulp.task('fonts', () => {
  return gulp.src(path.join(BOOTSTRAP_DIR, 'assets/fonts/**/*'))
    .pipe(gulp.dest('./build/fonts'));
});

gulp.task('eslint', () => {
  return gulp.src(JS_FILES)
    .pipe(eslint())
    .pipe(eslint.format());
});

gulp.task('watch', () => {
  watcher.on('update', (filenames) => {
    filenames.forEach((filename) => {
      console.log(path.relative(__dirname, filename) + ' changed.');
    });
    buildJS();
  });
  gulp.watch('./sass/**/*.scss', ['sass']);
  gulp.watch('./css/**/*.css', notifyLiveReload);
});

gulp.task('clean', () => {
  return gulp.src('build', {read: false}).pipe(clean());
});

gulp.task('default', ['eslint', 'browserify', 'fonts', 'sass', 'watch']);
